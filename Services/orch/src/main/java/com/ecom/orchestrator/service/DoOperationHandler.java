package com.ecom.orchestrator.service;

import com.ecom.orchestrator.dto.ExecutionMessage;
import com.ecom.orchestrator.entity.*;
import com.ecom.orchestrator.messaging.interfaces.MessagePublisher;
import com.ecom.orchestrator.repository.OrchestrationRunRepository;
import com.ecom.orchestrator.repository.OrchestrationStepRunRepository;
import com.ecom.orchestrator.repository.OrchestrationTemplateRepository;
import com.ecom.orchestrator.repository.WorkerRegistrationRepository;
import com.ecom.orchestrator.util.MessageHeaderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ecom.orchestrator.constant.RegistrationConstants.ORCHESTRATOR_EVENT;

/**
 * Service to handle DO operations for orchestration steps
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DoOperationHandler {

    private final OrchestrationRunRepository orchestrationRunRepository;
    private final OrchestrationStepRunRepository stepRunRepository;
    private final OrchestrationTemplateRepository orchestrationTemplateRepository;
    private final WorkerRegistrationRepository workerRegistrationRepository;
    private final MessagePublisher messagePublisher;
    private final UndoOperationHandler undoOperationHandler;
    private final AuditService auditService;

    /**
     * Handle DO response from worker
     */
    @Transactional
    public void handleDoResponse(String flowId, String stepName, boolean success, String errorMessage, ExecutionMessage message) {
        log.info("Handling DO response: flowId={}, stepName={}, success={}", flowId, stepName, success);

        Optional<OrchestrationRun> runOpt = orchestrationRunRepository.findByFlowIdWithSteps(flowId);
        if (runOpt.isEmpty()) {
            log.error("Orchestration run not found for flowId: {}", flowId);
            return;
        }

        OrchestrationRun orchestrationRun = runOpt.get();
        Optional<OrchestrationStepRun> stepRunOpt = stepRunRepository
                .findByOrchestrationRunFlowIdAndStepName(flowId, stepName);

        if (stepRunOpt.isEmpty()) {
            log.error("Step run not found: flowId={}, stepName={}", flowId, stepName);
            return;
        }

        OrchestrationStepRun stepRun = stepRunOpt.get();

        if (success) {
            handleDoSuccess(orchestrationRun, stepRun, message);
        } else {
            handleDoFailure(orchestrationRun, stepRun, errorMessage, message);
        }
    }

    /**
     * Handle successful DO operation
     */
    private void handleDoSuccess(OrchestrationRun orchestrationRun, OrchestrationStepRun stepRun, ExecutionMessage message) {
        log.info("DO operation successful: flowId={}, stepName={}", orchestrationRun.getFlowId(), stepRun.getStepName());

        // Calculate duration
        Long durationMs = null;
        if (stepRun.getStartedAt() != null) {
            durationMs = java.time.Duration.between(stepRun.getStartedAt(), LocalDateTime.now()).toMillis();
        }

        // Update step status to DO_SUCCESS
        stepRun.setStatus(ExecutionStatusEnum.DO_SUCCESS);
        stepRun.setCompletedAt(LocalDateTime.now());
        stepRun.setErrorMessage(null);
        stepRunRepository.save(stepRun);

        // Record audit event for step success
        auditService.recordStepSuccess(
            orchestrationRun.getFlowId(),
            orchestrationRun.getOrchName(),
            stepRun.getStepName(),
            stepRun.getWorkerService(),
            durationMs,
            "DO",
            stepRun.getRetryCount()
        );

        // Get orchestration template
        Optional<OrchestrationTemplate> templateOpt = orchestrationTemplateRepository
                .findByOrchNameWithSteps(orchestrationRun.getOrchName());

        if (templateOpt.isEmpty()) {
            log.error("Orchestration template not found: {}", orchestrationRun.getOrchName());
            return;
        }

        OrchestrationTemplate template = templateOpt.get();

        // Proceed to next step based on orchestration type
        if (template.getType() == OrchestrationTypeEnum.SEQUENTIAL) {
            handleSequentialDoSuccess(orchestrationRun, template, stepRun.getStepName(), message);
        } else {
            handleParallelDoSuccess(orchestrationRun);
        }
    }

    /**
     * Handle DO failure
     */
    private void handleDoFailure(OrchestrationRun orchestrationRun, OrchestrationStepRun stepRun,
                                  String errorMessage, ExecutionMessage message) {
        log.warn("DO operation failed: flowId={}, stepName={}, error={}",
                orchestrationRun.getFlowId(), stepRun.getStepName(), errorMessage);

        stepRun.setErrorMessage(errorMessage);

        // Record audit event for step failure
        auditService.recordStepFailure(
            orchestrationRun.getFlowId(),
            orchestrationRun.getOrchName(),
            stepRun.getStepName(),
            stepRun.getWorkerService(),
            errorMessage,
            stepRun.getRetryCount(),
            "DO"
        );

        // Check if we can retry
        if (stepRun.getRetryCount() < stepRun.getMaxRetries()) {
            retryDoOperation(orchestrationRun, stepRun, message);
        } else {
            handleRetryExhausted(orchestrationRun, stepRun, message);
        }
    }

    /**
     * Retry DO operation
     */
    private void retryDoOperation(OrchestrationRun orchestrationRun, OrchestrationStepRun stepRun, ExecutionMessage message) {
        stepRun.setRetryCount(stepRun.getRetryCount() + 1);
        stepRun.setStatus(ExecutionStatusEnum.IN_PROGRESS);
        stepRun.setLastRetryAt(LocalDateTime.now());
        stepRunRepository.save(stepRun);

        log.info("Retrying DO operation: flowId={}, stepName={}, attempt={}/{}",
                orchestrationRun.getFlowId(), stepRun.getStepName(),
                stepRun.getRetryCount(), stepRun.getMaxRetries());

        // Record audit event for retry attempt
        auditService.recordRetryAttempt(
            orchestrationRun.getFlowId(),
            orchestrationRun.getOrchName(),
            stepRun.getStepName(),
            stepRun.getRetryCount(),
            stepRun.getMaxRetries(),
            "DO",
            5000L  // Default backoff time in ms (configurable)
        );

        // Get step template to find DO topic
        Optional<OrchestrationTemplate> templateOpt = orchestrationTemplateRepository
                .findByOrchNameWithSteps(orchestrationRun.getOrchName());

        if (templateOpt.isEmpty()) {
            log.error("Orchestration template not found: {}", orchestrationRun.getOrchName());
            return;
        }

        OrchestrationTemplate template = templateOpt.get();
        Optional<OrchestrationStepTemplate> stepTemplateOpt = template.getSteps().stream()
                .filter(st -> st.getStepName().equals(stepRun.getStepName()))
                .findFirst();

        if (stepTemplateOpt.isEmpty()) {
            log.error("Step template not found: {}", stepRun.getStepName());
            return;
        }

        OrchestrationStepTemplate stepTemplate = stepTemplateOpt.get();

        // Send message to DO topic
        sendDoMessage(orchestrationRun.getFlowId(), stepTemplate, message);
    }

    /**
     * Handle retry exhausted scenario
     */
    private void handleRetryExhausted(OrchestrationRun orchestrationRun, OrchestrationStepRun stepRun, ExecutionMessage message) {
        log.error("DO operation retry exhausted: flowId={}, stepName={}",
                orchestrationRun.getFlowId(), stepRun.getStepName());

        // Update step status to RETRY_EXHAUSTED
        stepRun.setStatus(ExecutionStatusEnum.RETRY_EXHAUSTED);
        stepRun.setCompletedAt(LocalDateTime.now());
        stepRunRepository.save(stepRun);

        // Update orchestration run status to FAILED
        orchestrationRun.setStatus(ExecutionStatusEnum.FAILED);
        orchestrationRun.setCompletedAt(LocalDateTime.now());
        orchestrationRunRepository.save(orchestrationRun);

        // Record audit events
        auditService.recordStepFailure(
            orchestrationRun.getFlowId(),
            orchestrationRun.getOrchName(),
            stepRun.getStepName(),
            stepRun.getWorkerService(),
            "Retry exhausted after " + stepRun.getMaxRetries() + " attempts",
            stepRun.getRetryCount(),
            "DO"
        );

        auditService.recordOrchestrationFailure(
            orchestrationRun.getFlowId(),
            orchestrationRun.getOrchName(),
            "Step " + stepRun.getStepName() + " failed after retry exhaustion"
        );

        auditService.recordRollbackTriggered(
            orchestrationRun.getFlowId(),
            orchestrationRun.getOrchName(),
            stepRun.getStepName(),
            "Rollback triggered due to retry exhaustion"
        );

        // Trigger UNDO for all successfully completed steps
        log.info("Triggering UNDO for completed steps due to retry exhaustion");
        undoOperationHandler.undoOrchestration(orchestrationRun.getFlowId(), message);
    }

    /**
     * Handle sequential DO success - execute next step
     */
    private void handleSequentialDoSuccess(OrchestrationRun orchestrationRun, OrchestrationTemplate template,
                                           String completedStepName, ExecutionMessage message) {
        log.info("Handling sequential DO success: flowId={}, completedStep={}",
                orchestrationRun.getFlowId(), completedStepName);

        // Find next step
        List<OrchestrationStepTemplate> sortedSteps = template.getSteps().stream()
                .sorted(Comparator.comparing(OrchestrationStepTemplate::getSeq))
                .toList();

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
            OrchestrationStepTemplate nextStep = nextStepOpt.get();
            log.info("Executing next step: {}", nextStep.getStepName());

            // Update next step status to IN_PROGRESS
            updateStepRunStatus(orchestrationRun.getFlowId(), nextStep.getStepName(),
                    ExecutionStatusEnum.IN_PROGRESS, nextStep.getMaxRetries());

            sendDoMessage(orchestrationRun.getFlowId(), nextStep, message);
        } else {
            // All steps completed successfully
            completeOrchestration(orchestrationRun.getFlowId());
        }
    }

    /**
     * Handle parallel DO success - check if all steps completed
     */
    private void handleParallelDoSuccess(OrchestrationRun orchestrationRun) {
        log.info("Handling parallel DO success: flowId={}", orchestrationRun.getFlowId());

        // Check if all steps are completed successfully
        boolean allCompleted = orchestrationRun.getStepRuns().stream()
                .allMatch(stepRun -> stepRun.getStatus() == ExecutionStatusEnum.DO_SUCCESS);

        if (allCompleted) {
            completeOrchestration(orchestrationRun.getFlowId());
        }
    }

    /**
     * Send DO message to worker
     */
    public void sendDoMessage(String flowId, OrchestrationStepTemplate stepTemplate, ExecutionMessage message) {
        log.info("Sending DO message: flowId={}, stepName={}, sharedtopic={}",
                flowId, stepTemplate.getStepName(), stepTemplate.getSharedTopic());

        // Get orchestration name and worker service for audit
        Optional<OrchestrationRun> runOpt = orchestrationRunRepository.findByFlowId(flowId);
        if (runOpt.isPresent()) {
            String orchName = runOpt.get().getOrchName();
            Optional<OrchestrationStepRun> stepRunOpt = stepRunRepository
                    .findByOrchestrationRunFlowIdAndStepName(flowId, stepTemplate.getStepName());

            if (stepRunOpt.isPresent()) {
                String workerService = stepRunOpt.get().getWorkerService();

                // Record audit event for step start
                auditService.recordStepStart(flowId, orchName, stepTemplate.getStepName(), workerService, "DO");
            }
        }

        try {
            message.getHeaders().put("stepName", stepTemplate.getStepName());
            message.getHeaders().put("eventType", "do"+stepTemplate.getStepName());
            messagePublisher.send(Boolean.TRUE.equals(stepTemplate.getSharedTopic())?ORCHESTRATOR_EVENT:stepTemplate.getDoTopic(), message);
            log.info("DO message sent successfully: flowId={}, stepName={}", flowId, stepTemplate.getStepName());
        } catch (Exception e) {
            log.error("Failed to send DO message: flowId={}, stepName={}", flowId, stepTemplate.getStepName(), e);
            // Update step status to failed
            Optional<OrchestrationStepRun> stepRunOpt = stepRunRepository
                    .findByOrchestrationRunFlowIdAndStepName(flowId, stepTemplate.getStepName());

            if (stepRunOpt.isPresent()) {
                handleDoFailure(orchestrationRunRepository.findByFlowId(flowId).get(),
                        stepRunOpt.get(), "Failed to send DO message: " + e.getMessage(), message);
            }
        }
    }

    /**
     * Start DO operations for orchestration
     */
    @Transactional
    public void startDoOperations(String flowId, OrchestrationTemplate template, ExecutionMessage message) {
        log.info("Starting DO operations: flowId={}, orchName={}, type={}",
                flowId, template.getOrchName(), template.getType());

        if (template.getType() == OrchestrationTypeEnum.SEQUENTIAL) {
            // Execute first step only
            List<OrchestrationStepTemplate> sortedSteps = template.getSteps().stream()
                    .sorted(Comparator.comparing(OrchestrationStepTemplate::getSeq))
                    .toList();

            if (!sortedSteps.isEmpty()) {
                OrchestrationStepTemplate firstStep = sortedSteps.getFirst();
                updateStepRunStatus(flowId, firstStep.getStepName(), ExecutionStatusEnum.IN_PROGRESS, firstStep.getMaxRetries());
                sendDoMessage(flowId, firstStep, message);
            }
        } else {
            // Execute all steps in parallel
            for (OrchestrationStepTemplate stepTemplate : template.getSteps()) {
                updateStepRunStatus(flowId, stepTemplate.getStepName(), ExecutionStatusEnum.IN_PROGRESS, stepTemplate.getMaxRetries());
                sendDoMessage(flowId, stepTemplate, message);
            }
        }
    }

    /**
     * Update step run status
     */
    private void updateStepRunStatus(String flowId, String stepName, ExecutionStatusEnum status, Integer maxRetries) {
        Optional<OrchestrationStepRun> stepRunOpt = stepRunRepository
                .findByOrchestrationRunFlowIdAndStepName(flowId, stepName);

        if (stepRunOpt.isPresent()) {
            OrchestrationStepRun stepRun = stepRunOpt.get();
            stepRun.setStatus(status);

            if (maxRetries != null) {
                stepRun.setMaxRetries(maxRetries);
            }

            if (status == ExecutionStatusEnum.IN_PROGRESS && stepRun.getStartedAt() == null) {
                stepRun.setStartedAt(LocalDateTime.now());
            }

            stepRunRepository.save(stepRun);
            log.info("Step run status updated: flowId={}, stepName={}, status={}", flowId, stepName, status);
        }
    }

    /**
     * Complete orchestration
     */
    private void completeOrchestration(String flowId) {
        log.info("Completing orchestration: flowId={}", flowId);

        Optional<OrchestrationRun> runOpt = orchestrationRunRepository.findByFlowId(flowId);
        if (runOpt.isPresent()) {
            OrchestrationRun run = runOpt.get();

            // Calculate total duration
            Long durationMs = null;
            if (run.getStartedAt() != null) {
                durationMs = java.time.Duration.between(run.getStartedAt(), LocalDateTime.now()).toMillis();
            }

            run.setStatus(ExecutionStatusEnum.COMPLETED);
            run.setCompletedAt(LocalDateTime.now());
            orchestrationRunRepository.save(run);

            // Record audit event for orchestration completion
            auditService.recordOrchestrationComplete(
                flowId,
                run.getOrchName(),
                "SUCCESS",
                durationMs
            );

            log.info("Orchestration completed successfully: flowId={}", flowId);
        }
    }
}

