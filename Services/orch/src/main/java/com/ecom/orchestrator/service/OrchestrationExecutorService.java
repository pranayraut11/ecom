package com.ecom.orchestrator.service;

import com.ecom.orchestrator.dto.ExecutionMessage;
import com.ecom.orchestrator.dto.OrchestrationEventDto;
import com.ecom.orchestrator.entity.*;
import com.ecom.orchestrator.repository.*;
import com.ecom.orchestrator.messaging.interfaces.MessagePublisher;
import com.ecom.orchestrator.serialization.Serializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrchestrationExecutorService {

    private final OrchestrationTemplateRepository orchestrationTemplateRepository;
    private final WorkerRegistrationRepository workerRegistrationRepository;
    private final OrchestrationRunRepository orchestrationRunRepository;
    private final OrchestrationStepRunRepository stepRunRepository;

    private final MessagePublisher messagePublisher;


    private final UndoService undoService;

    public OrchestrationExecutorService(
            OrchestrationTemplateRepository orchestrationTemplateRepository,
            WorkerRegistrationRepository workerRegistrationRepository,
            OrchestrationRunRepository orchestrationRunRepository,
            OrchestrationStepRunRepository stepRunRepository,
            MessagePublisher messagePublisher,
            UndoService undoService) {

        this.orchestrationTemplateRepository = orchestrationTemplateRepository;

        this.workerRegistrationRepository = workerRegistrationRepository;
        this.orchestrationRunRepository = orchestrationRunRepository;
        this.stepRunRepository = stepRunRepository;
        this.messagePublisher = messagePublisher;
        this.undoService = undoService;
    }


    @Transactional
    public String startOrchestration(String orchName, ExecutionMessage message) {
        log.info("Starting orchestration: {}", orchName);

        // Validate orchestration exists and is ready
        Optional<OrchestrationTemplate> templateOpt = orchestrationTemplateRepository
                .findByOrchNameWithSteps(orchName);

        // Generate flow ID
        String flowId = message.getHeaders().get("flowId") != null ?
                message.getHeaders().get("flowId").toString() : UUID.randomUUID().toString();
        // Create orchestration run
        OrchestrationRun orchestrationRun = OrchestrationRun.builder()
                .flowId(flowId)
                .orchName(orchName)
                .status(ExecutionStatusEnum.NOT_REGISTERED)
                .build();
        if (templateOpt.isEmpty()) {
            throw new IllegalArgumentException("Orchestration not found: " + orchName);
        }

        OrchestrationTemplate template = templateOpt.get();

        if (template.getStatus() != OrchestrationStatusEnum.SUCCESS) {
            orchestrationRunRepository.save(orchestrationRun);
            log.info("Orchestration not ready: " + orchName +
                    " Status: " + template.getStatus());
        }else {

            // Create orchestration run
            orchestrationRun = OrchestrationRun.builder()
                    .flowId(flowId)
                    .orchName(orchName)
                    .status(ExecutionStatusEnum.IN_PROGRESS)
                    .build();

            final OrchestrationRun savedOrchestrationRun = orchestrationRunRepository.save(orchestrationRun);

            // Create step runs
            List<OrchestrationStepRun> stepRuns = template.getSteps().stream()
                    .map(stepTemplate -> OrchestrationStepRun.builder()
                            .orchestrationRun(savedOrchestrationRun)
                            .stepName(stepTemplate.getStepName())
                            .seq(stepTemplate.getSeq())
                            .status(ExecutionStatusEnum.PENDING)
                            .build())
                    .collect(Collectors.toList());

            stepRunRepository.saveAll(stepRuns);
            savedOrchestrationRun.setStepRuns(stepRuns);

            // Execute based on orchestration type
            if (template.getType() == OrchestrationTypeEnum.SEQUENTIAL) {
                executeSequential(flowId, template, message);
            } else {
                executeSimultaneous(flowId, template, message);
            }

        }
        return flowId;
    }

    private void executeSequential(String flowId, OrchestrationTemplate template,ExecutionMessage message) {
        log.info("Executing sequential orchestration: {} flowId: {}", template.getOrchName(), flowId);

        List<OrchestrationStepTemplate> sortedSteps = template.getSteps().stream()
                .sorted(Comparator.comparing(OrchestrationStepTemplate::getSeq))
                .collect(Collectors.toList());

        if (!sortedSteps.isEmpty()) {
            executeStep(flowId, sortedSteps.get(0), message);
        }
    }

    private void executeSimultaneous(String flowId, OrchestrationTemplate template,ExecutionMessage message) {
        log.info("Executing simultaneous orchestration: {} flowId: {}", template.getOrchName(), flowId);

        // Execute all steps simultaneously
        for (OrchestrationStepTemplate stepTemplate : template.getSteps()) {
            executeStep(flowId, stepTemplate, message);
        }
    }

    private void executeStep(String flowId, OrchestrationStepTemplate stepTemplate,ExecutionMessage message) {
        log.info("Executing step: {} flowId: {}", stepTemplate.getStepName(), flowId);
        message.getHeaders().put("stepName",stepTemplate.getStepName());
        // Find worker for this step
        Optional<WorkerRegistration> workerOpt = workerRegistrationRepository
                .findByOrchNameAndStepName(stepTemplate.getTemplate().getOrchName(), stepTemplate.getStepName());

        if (workerOpt.isEmpty()) {
            handleStepFailure(flowId, stepTemplate.getStepName(), "No worker registered for step");
            return;
        }

        WorkerRegistration worker = workerOpt.get();

        // Update step run status
        updateStepRunStatus(flowId, stepTemplate.getStepName(), ExecutionStatusEnum.IN_PROGRESS,
                worker.getWorkerService(), null);

        try {
            messagePublisher.send(stepTemplate.getTopicName(), message);
            log.info("Sent execution event for step: {} flowId: {} to topic: {}",
                    stepTemplate.getStepName(), flowId, stepTemplate.getTopicName());
        } catch (Exception e) {
            log.error("Failed to send execution event for step: {} flowId: {}",
                    stepTemplate.getStepName(), flowId, e);
            handleStepFailure(flowId, stepTemplate.getStepName(), "Failed to send execution event: " + e.getMessage());
        }
    }

    //@Transactional
    public void handleStepResponse(String flowId, String stepName, boolean success, String errorMessage,ExecutionMessage message) {
        log.info("Handling step response: flowId: {} step: {} success: {}", flowId, stepName, success);

        Optional<OrchestrationRun> runOpt = orchestrationRunRepository.findByFlowIdWithSteps(flowId);
        if (runOpt.isEmpty()) {
            log.error("Orchestration run not found: {}", flowId);
            return;
        }

        OrchestrationRun orchestrationRun = runOpt.get();

        if (success) {
            handleStepSuccess(orchestrationRun, stepName,message);
        } else {
            handleStepFailure(flowId, stepName, errorMessage);
        }
    }

    private void handleStepSuccess(OrchestrationRun orchestrationRun, String stepName,ExecutionMessage message) {
        // Update step run status
        updateStepRunStatus(orchestrationRun.getFlowId(), stepName, ExecutionStatusEnum.COMPLETED, null, null);

        // Get orchestration template
        Optional<OrchestrationTemplate> templateOpt = orchestrationTemplateRepository
                .findByOrchNameWithSteps(orchestrationRun.getOrchName());

        if (templateOpt.isEmpty()) {
            log.error("Orchestration template not found: {}", orchestrationRun.getOrchName());
            return;
        }

        OrchestrationTemplate template = templateOpt.get();

        if (template.getType() == OrchestrationTypeEnum.SEQUENTIAL) {
            handleSequentialStepSuccess(orchestrationRun, template, stepName,message);
        } else {
            handleSimultaneousStepSuccess(orchestrationRun, template);
        }
    }

    private void handleSequentialStepSuccess(OrchestrationRun orchestrationRun,
                                            OrchestrationTemplate template, String completedStepName,ExecutionMessage message) {
        log.info("Handling sequential step success: flowId: {} completedStep: {}",
                orchestrationRun.getFlowId(), completedStepName);
        // Find next step
        List<OrchestrationStepTemplate> sortedSteps = template.getSteps().stream()
                .sorted(Comparator.comparing(OrchestrationStepTemplate::getSeq))
                .collect(Collectors.toList());

        Optional<OrchestrationStepTemplate> nextStepOpt = sortedSteps.stream()
                .filter(step -> {
                    OrchestrationStepRun stepRun = orchestrationRun.getStepRuns().stream()
                            .filter(sr -> sr.getStepName().equals(step.getStepName()))
                            .findFirst()
                            .orElse(null);
                    return stepRun != null && stepRun.getStatus() == ExecutionStatusEnum.PENDING;
                })
                .findFirst();

        if (nextStepOpt.isPresent()) {
            // Execute next step
            executeStep(orchestrationRun.getFlowId(), nextStepOpt.get(),message); // Use empty payload for subsequent steps
        } else {
            // All steps completed
            completeOrchestration(orchestrationRun.getFlowId());
        }
    }

    private void handleSimultaneousStepSuccess(OrchestrationRun orchestrationRun, OrchestrationTemplate template) {
        // Check if all steps are completed
        boolean allCompleted = orchestrationRun.getStepRuns().stream()
                .allMatch(stepRun -> stepRun.getStatus() == ExecutionStatusEnum.COMPLETED);

        if (allCompleted) {
            completeOrchestration(orchestrationRun.getFlowId());
        }
    }

    private void handleStepFailure(String flowId, String stepName, String errorMessage) {
        log.error("Step failed: flowId: {} step: {} error: {}", flowId, stepName, errorMessage);

        // Update step run status
        updateStepRunStatus(flowId, stepName, ExecutionStatusEnum.FAILED, null, errorMessage);

        // Update orchestration run status
        updateOrchestrationRunStatus(flowId, ExecutionStatusEnum.FAILED);

        // Trigger undo for completed steps
        undoService.undoOrchestration(flowId);
    }

    private void completeOrchestration(String flowId) {
        log.info("Completing orchestration: flowId: {}", flowId);
        updateOrchestrationRunStatus(flowId, ExecutionStatusEnum.COMPLETED);
    }

    private void updateStepRunStatus(String flowId, String stepName, ExecutionStatusEnum status,
                                    String workerService, String errorMessage) {
        log.info("Updating step run status: flowId: {} step: {} status: {}", flowId, stepName, status);
        Optional<OrchestrationStepRun> stepRunOpt = stepRunRepository
                .findByOrchestrationRunFlowIdAndStepName(flowId, stepName);

        if (stepRunOpt.isPresent()) {
            OrchestrationStepRun stepRun = stepRunOpt.get();
            stepRun.setStatus(status);
            if (workerService != null) {
                stepRun.setWorkerService(workerService);
            }
            if (errorMessage != null) {
                stepRun.setErrorMessage(errorMessage);
            }

            LocalDateTime now = LocalDateTime.now();
            if (status == ExecutionStatusEnum.IN_PROGRESS && stepRun.getStartedAt() == null) {
                stepRun.setStartedAt(now);
            } else if (status == ExecutionStatusEnum.COMPLETED || status == ExecutionStatusEnum.FAILED) {
                stepRun.setCompletedAt(now);
            }

            stepRunRepository.saveAndFlush(stepRun);
            log.info("Step run status updated: flowId: {} step: {} status: {}", flowId, stepName, status);
        }
    }

    private void updateOrchestrationRunStatus(String flowId, ExecutionStatusEnum status) {
        Optional<OrchestrationRun> runOpt = orchestrationRunRepository.findByFlowId(flowId);
        if (runOpt.isPresent()) {
            OrchestrationRun run = runOpt.get();
            run.setStatus(status);
            if (status == ExecutionStatusEnum.COMPLETED || status == ExecutionStatusEnum.FAILED) {
                run.setCompletedAt(LocalDateTime.now());
            }
            orchestrationRunRepository.save(run);
        }
    }

}
