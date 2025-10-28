package com.ecom.orchestrator.service;

import com.ecom.orchestrator.dto.ExecutionMessage;
import com.ecom.orchestrator.dto.OrchestrationEventDto;
import com.ecom.orchestrator.entity.*;
import com.ecom.orchestrator.repository.*;
import com.ecom.orchestrator.messaging.interfaces.MessagePublisher;
import com.ecom.orchestrator.serialization.Serializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.messaging.support.MessageBuilder.withPayload;

@Service
@Slf4j
public class UndoService {

    private final OrchestrationRunRepository orchestrationRunRepository;
    private final OrchestrationStepRunRepository stepRunRepository;
    private final OrchestrationStepTemplateRepository stepTemplateRepository;
    private final WorkerRegistrationRepository workerRegistrationRepository;
    private final OrchestrationTemplateRepository orchestrationTemplateRepository;
    private final MessagePublisher messagePublisher;

    public UndoService(OrchestrationRunRepository orchestrationRunRepository, OrchestrationStepRunRepository stepRunRepository, OrchestrationStepTemplateRepository stepTemplateRepository, WorkerRegistrationRepository workerRegistrationRepository, OrchestrationTemplateRepository orchestrationTemplateRepository, MessagePublisher messagePublisher) {
        this.orchestrationRunRepository = orchestrationRunRepository;
        this.stepRunRepository = stepRunRepository;
        this.stepTemplateRepository = stepTemplateRepository;
        this.workerRegistrationRepository = workerRegistrationRepository;
        this.orchestrationTemplateRepository = orchestrationTemplateRepository;
        this.messagePublisher = messagePublisher;
    }

    @Transactional
    public void undoOrchestration(String flowId) {
        log.info("Starting undo for orchestration: flowId: {}", flowId);

        Optional<OrchestrationRun> runOpt = orchestrationRunRepository.findByFlowIdWithSteps(flowId);
        if (runOpt.isEmpty()) {
            log.error("Orchestration run not found for undo: {}", flowId);
            return;
        }

        OrchestrationRun orchestrationRun = runOpt.get();

        // Update orchestration status to undoing
        orchestrationRun.setStatus(ExecutionStatusEnum.UNDOING);
        orchestrationRunRepository.save(orchestrationRun);

        // Get completed steps that need to be undone (in reverse order)
        List<OrchestrationStepRun> completedSteps = orchestrationRun.getStepRuns().stream()
                .filter(stepRun -> stepRun.getStatus() == ExecutionStatusEnum.COMPLETED)
                .sorted(Comparator.comparing(OrchestrationStepRun::getSeq).reversed())
                .collect(Collectors.toList());

        if (completedSteps.isEmpty()) {
            log.info("No completed steps to undo for flowId: {}", flowId);
            completeUndo(flowId);
            return;
        }

        // Send undo events for all completed steps
        for (OrchestrationStepRun stepRun : completedSteps) {
            sendUndoEvent(orchestrationRun, stepRun);
        }
    }

    public void sendUndoEvent(OrchestrationRun orchestrationRun, OrchestrationStepRun stepRun) {
        try {
            // Get step template for topic name
            Optional<OrchestrationStepTemplate> stepTemplateOpt = stepTemplateRepository
                    .findByTemplateOrchNameAndStepName(orchestrationRun.getOrchName(), stepRun.getStepName());

            if (stepTemplateOpt.isEmpty()) {
                log.error("Step template not found for undo: {} {}",
                        orchestrationRun.getOrchName(), stepRun.getStepName());
                return;
            }

            OrchestrationStepTemplate stepTemplate = stepTemplateOpt.get();

            // Get worker registration
            Optional<WorkerRegistration> workerOpt = workerRegistrationRepository
                    .findByOrchNameAndStepName(orchestrationRun.getOrchName(), stepRun.getStepName());

            if (workerOpt.isEmpty()) {
                log.error("Worker registration not found for undo: {} {}",
                        orchestrationRun.getOrchName(), stepRun.getStepName());
                return;
            }

            WorkerRegistration worker = workerOpt.get();

            // Create metadata
            Map<String, Object> metadata = Map.of(
                    "initiator", stepTemplate.getTemplate().getInitiatorService(),
                    "worker", worker.getWorkerService(),
                    "timestamp", LocalDateTime.now().toString(),
                    "originalCompletedAt", stepRun.getCompletedAt().toString()
            );

            // Create undo event
            OrchestrationEventDto undoEvent = OrchestrationEventDto.builder()
                    .flowId(orchestrationRun.getFlowId())
                    .orchName(orchestrationRun.getOrchName())
                    .stepName(stepRun.getStepName())
                    .action("UNDO")
                    .payload(new byte[0]) // Empty payload for undo
                    .metadata(metadata)
                    .build();

            // Serialize and create Message object with headers

            HashMap<String, Object> headers = new HashMap<>();
            headers.put("flowId", orchestrationRun.getFlowId());
            headers.put("orchName", orchestrationRun.getOrchName());
            headers.put("stepName", stepRun.getStepName());
            headers.put("action", "UNDO");
            headers.put("initiator", stepTemplate.getTemplate().getInitiatorService());
            headers.put("worker", worker.getWorkerService());
            headers.put("timestamp", LocalDateTime.now().toString());
            headers.put("originalCompletedAt", stepRun.getCompletedAt().toString());

            ExecutionMessage undoMessage = new ExecutionMessage(undoEvent, headers);

            messagePublisher.send(stepTemplate.getTopicName(), undoMessage);

            log.info("Sent undo event for step: {} flowId: {}", stepRun.getStepName(), orchestrationRun.getFlowId());

        } catch (Exception e) {
            log.error("Failed to send undo event for step: {} flowId: {}",
                    stepRun.getStepName(), orchestrationRun.getFlowId(), e);
        }
    }

    @Transactional
    public void handleUndoResponse(String flowId, String stepName, boolean success, String errorMessage) {
        log.info("Handling undo response: flowId: {} step: {} success: {}", flowId, stepName, success);

        Optional<OrchestrationStepRun> stepRunOpt = stepRunRepository
                .findByOrchestrationRunFlowIdAndStepName(flowId, stepName);

        if (stepRunOpt.isEmpty()) {
            log.error("Step run not found for undo response: flowId: {} step: {}", flowId, stepName);
            return;
        }

        OrchestrationStepRun stepRun = stepRunOpt.get();

        if (success) {
            // Mark step as undone
            stepRun.setUndoneAt(LocalDateTime.now());
            stepRunRepository.save(stepRun);

            log.info("Step successfully undone: flowId: {} step: {}", flowId, stepName);

            triggerUndoForRemainingSteps(flowId, stepName);
            // Check if all completed steps have been undone
            checkUndoCompletion(flowId);
        } else {
            log.error("Failed to undo step: flowId: {} step: {} error: {}", flowId, stepName, errorMessage);
            // Continue with undo process even if one step fails
            checkUndoCompletion(flowId);
        }
    }

    private void checkUndoCompletion(String flowId) {
        Optional<OrchestrationRun> runOpt = orchestrationRunRepository.findByFlowIdWithSteps(flowId);
        if (runOpt.isEmpty()) {
            return;
        }

        OrchestrationRun orchestrationRun = runOpt.get();

        // Check if all completed steps have been processed for undo
        boolean allUndoProcessed = orchestrationRun.getStepRuns().stream()
                .filter(stepRun -> stepRun.getStatus() == ExecutionStatusEnum.COMPLETED)
                .allMatch(stepRun -> stepRun.getUndoneAt() != null);

        if (allUndoProcessed) {
            completeUndo(flowId);
        }
    }

    private void completeUndo(String flowId) {
        log.info("Completing undo for orchestration: flowId: {}", flowId);

        Optional<OrchestrationRun> runOpt = orchestrationRunRepository.findByFlowId(flowId);
        if (runOpt.isPresent()) {
            OrchestrationRun run = runOpt.get();
            run.setStatus(ExecutionStatusEnum.UNDONE);
            run.setCompletedAt(LocalDateTime.now());
            orchestrationRunRepository.save(run);

            log.info("Undo completed for orchestration: flowId: {}", flowId);
        }
    }

    @Transactional
    public void manualUndo(String flowId) {
        log.info("Manual undo triggered for flowId: {}", flowId);
        undoOrchestration(flowId);
    }

    public void triggerUndoForRemainingSteps(String flowId, String completedStepName) {
        log.info("Triggering undo for remaining steps after successful undo of step: {} flowId: {}", completedStepName, flowId);

        Optional<OrchestrationRun> runOpt = orchestrationRunRepository.findByFlowIdWithSteps(flowId);
        if (runOpt.isEmpty()) {
            log.error("Orchestration run not found: {}", flowId);
            return;
        }

        OrchestrationRun orchestrationRun = runOpt.get();

        // Get orchestration template to determine type
        Optional<OrchestrationTemplate> templateOpt = orchestrationTemplateRepository
                .findByOrchNameWithSteps(orchestrationRun.getOrchName());

        if (templateOpt.isEmpty()) {
            log.error("Orchestration template not found: {}", orchestrationRun.getOrchName());
            return;
        }

        OrchestrationTemplate template = templateOpt.get();
        OrchestrationTypeEnum orchestrationType = template.getType(); // Store the type

        // Get the sequence number of the completed undo step
        Optional<OrchestrationStepRun> completedStepOpt = orchestrationRun.getStepRuns().stream()
                .filter(stepRun -> stepRun.getStepName().equals(completedStepName))
                .findFirst();

        if (completedStepOpt.isEmpty()) {
            log.error("Completed step not found: {} in flowId: {}", completedStepName, flowId);
            return;
        }

        int completedStepSeq = completedStepOpt.get().getSeq();

        if (orchestrationType == OrchestrationTypeEnum.SEQUENTIAL) {
            // Sequential: Send undo for next single step
            Optional<OrchestrationStepRun> nextStepToUndo = orchestrationRun.getStepRuns().stream()
                    .filter(stepRun -> stepRun.getStatus() == ExecutionStatusEnum.COMPLETED)
                    .filter(stepRun -> stepRun.getSeq() < completedStepSeq)
                    .filter(stepRun -> stepRun.getUndoneAt() == null)
                    .max(Comparator.comparing(OrchestrationStepRun::getSeq));

            if (nextStepToUndo.isPresent()) {
                sendUndoEvent(orchestrationRun, nextStepToUndo.get());
                log.info("Sequential: Triggered undo for next step: {} after step: {} flowId: {}",
                        nextStepToUndo.get().getStepName(), completedStepName, flowId);
            } else {
                log.info("Sequential: No more steps to undo after step: {} flowId: {}", completedStepName, flowId);
            }
        } else {
            // Simultaneous: All remaining steps at once
            List<OrchestrationStepRun> remainingSteps = orchestrationRun.getStepRuns().stream()
                    .filter(stepRun -> stepRun.getStatus() == ExecutionStatusEnum.COMPLETED)
                    .filter(stepRun -> stepRun.getSeq() < completedStepSeq)
                    .filter(stepRun -> stepRun.getUndoneAt() == null)
                    .sorted(Comparator.comparing(OrchestrationStepRun::getSeq).reversed())
                    .collect(Collectors.toList());

            for (OrchestrationStepRun stepRun : remainingSteps) {
                sendUndoEvent(orchestrationRun, stepRun);
            }

            log.info("Simultaneous: Triggered undo for {} remaining steps after step: {} flowId: {}",
                    remainingSteps.size(), completedStepName, flowId);
        }
    }
}
