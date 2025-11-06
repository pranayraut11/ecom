package com.ecom.orchestrator.service;

import com.ecom.orchestrator.dto.ExecutionMessage;
import com.ecom.orchestrator.entity.*;
import com.ecom.orchestrator.messaging.interfaces.MessagePublisher;
import com.ecom.orchestrator.repository.OrchestrationRunRepository;
import com.ecom.orchestrator.repository.OrchestrationStepRunRepository;
import com.ecom.orchestrator.repository.OrchestrationTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service to handle UNDO operations for orchestration steps
 */
@Service
@Slf4j
public class UndoOperationHandler {

    private final OrchestrationRunRepository orchestrationRunRepository;
    private final OrchestrationStepRunRepository stepRunRepository;
    private final OrchestrationTemplateRepository orchestrationTemplateRepository;
    private final MessagePublisher messagePublisher;
    private final AuditService auditService;

    public UndoOperationHandler(
            OrchestrationRunRepository orchestrationRunRepository,
            OrchestrationStepRunRepository stepRunRepository,
            OrchestrationTemplateRepository orchestrationTemplateRepository,
            MessagePublisher messagePublisher,
            AuditService auditService) {
        this.orchestrationRunRepository = orchestrationRunRepository;
        this.stepRunRepository = stepRunRepository;
        this.orchestrationTemplateRepository = orchestrationTemplateRepository;
        this.messagePublisher = messagePublisher;
        this.auditService = auditService;
    }

    /**
     * Handle UNDO response from worker
     */
    @Transactional
    public void handleUndoResponse(String flowId, String stepName, boolean success, String errorMessage, ExecutionMessage message) {
        log.info("Handling UNDO response: flowId={}, stepName={}, success={}", flowId, stepName, success);

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
            handleUndoSuccess(orchestrationRun, stepRun, message);
        } else {
            handleUndoFailure(orchestrationRun, stepRun, errorMessage, message);
        }
    }

    /**
     * Handle FAIL_STEP action from worker
     * When a worker explicitly sends FAIL_STEP action, it means:
     * 1. The current step has failed
     * 2. We need to mark it as FAILED
     * 3. Trigger UNDO for all previously completed steps
     */
    @Transactional
    public void handleFailResponse(String flowId, String stepName, boolean success, String errorMessage, ExecutionMessage message) {
        log.info("Handling FAIL_STEP action: flowId={}, stepName={}, error={}", flowId, stepName, errorMessage);

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

        log.warn("Step failed explicitly (FAIL_STEP action): flowId={}, stepName={}, error={}",
                orchestrationRun.getFlowId(), stepRun.getStepName(), errorMessage);

        // Update step status to FAILED
        stepRun.setStatus(ExecutionStatusEnum.FAILED);
        stepRun.setErrorMessage(errorMessage);
        stepRun.setCompletedAt(LocalDateTime.now());
        stepRunRepository.save(stepRun);
        log.info("Step marked as FAILED: flowId={}, stepName={}", flowId, stepName);

        // Update orchestration run status to FAILED
        orchestrationRun.setStatus(ExecutionStatusEnum.FAILED);
        orchestrationRunRepository.save(orchestrationRun);
        log.info("Orchestration run marked as FAILED: flowId={}", flowId);

        // Trigger UNDO for all successfully completed steps (DO_SUCCESS)
        List<OrchestrationStepRun> completedSteps = orchestrationRun.getStepRuns().stream()
                .filter(sr -> sr.getStatus() == ExecutionStatusEnum.DO_SUCCESS)
                .collect(Collectors.toList());

        if (!completedSteps.isEmpty()) {
            log.info("Triggering UNDO for {} successfully completed steps due to step failure", completedSteps.size());
            undoOrchestration(flowId, message);
        } else {
            log.info("No completed steps to undo for flowId: {}", flowId);
            // Complete the orchestration as failed
            orchestrationRun.setCompletedAt(LocalDateTime.now());
            orchestrationRunRepository.save(orchestrationRun);
            log.error("Orchestration failed with no steps to undo: flowId={}", flowId);
        }
    }
    /**
     * Handle successful UNDO operation
     */
    private void handleUndoSuccess(OrchestrationRun orchestrationRun, OrchestrationStepRun stepRun, ExecutionMessage message) {
        log.info("UNDO operation successful: flowId={}, stepName={}", orchestrationRun.getFlowId(), stepRun.getStepName());

        // Calculate duration
        Long durationMs = null;
        if (stepRun.getStartedAt() != null) {
            durationMs = java.time.Duration.between(stepRun.getStartedAt(), LocalDateTime.now()).toMillis();
        }

        // Update step status to UNDO_SUCCESS
        stepRun.setStatus(ExecutionStatusEnum.UNDO_SUCCESS);
        stepRun.setUndoneAt(LocalDateTime.now());
        stepRun.setErrorMessage(null);
        stepRun.setRetryCount(0); // Reset retry count for undo
        stepRunRepository.save(stepRun);

        // Record audit event for UNDO completion
        auditService.recordUndoComplete(
            orchestrationRun.getFlowId(),
            orchestrationRun.getOrchName(),
            stepRun.getStepName(),
            stepRun.getWorkerService(),
            durationMs
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
            handleSequentialUndoSuccess(orchestrationRun, template, stepRun.getStepName(), message);
        } else {
            handleParallelUndoSuccess(orchestrationRun);
        }
    }

    /**
     * Handle UNDO failure
     */
    private void handleUndoFailure(OrchestrationRun orchestrationRun, OrchestrationStepRun stepRun,
                                   String errorMessage, ExecutionMessage message) {
        log.warn("UNDO operation failed: flowId={}, stepName={}, error={}",
                orchestrationRun.getFlowId(), stepRun.getStepName(), errorMessage);

        stepRun.setErrorMessage(errorMessage);

        // Record audit event for UNDO failure
        auditService.recordUndoFailure(
            orchestrationRun.getFlowId(),
            orchestrationRun.getOrchName(),
            stepRun.getStepName(),
            stepRun.getWorkerService(),
            errorMessage
        );

        // Check if we can retry
        if (stepRun.getRetryCount() < stepRun.getMaxRetries()) {
            retryUndoOperation(orchestrationRun, stepRun, message);
        } else {
            handleUndoRetryExhausted(orchestrationRun, stepRun);
        }
    }

    /**
     * Retry UNDO operation
     */
    private void retryUndoOperation(OrchestrationRun orchestrationRun, OrchestrationStepRun stepRun, ExecutionMessage message) {
        stepRun.setRetryCount(stepRun.getRetryCount() + 1);
        stepRun.setStatus(ExecutionStatusEnum.UNDOING);
        stepRunRepository.save(stepRun);

        log.info("Retrying UNDO operation: flowId={}, stepName={}, attempt={}/{}",
                orchestrationRun.getFlowId(), stepRun.getStepName(),
                stepRun.getRetryCount(), stepRun.getMaxRetries());

        // Get step template to find UNDO topic
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

        // Send message to UNDO topic
        sendUndoMessage(orchestrationRun.getFlowId(), stepTemplate, message);
    }

    /**
     * Handle UNDO retry exhausted scenario
     */
    private void handleUndoRetryExhausted(OrchestrationRun orchestrationRun, OrchestrationStepRun stepRun) {
        log.error("UNDO operation retry exhausted: flowId={}, stepName={}",
                orchestrationRun.getFlowId(), stepRun.getStepName());

        // Update step status to UNDO_FAIL
        stepRun.setStatus(ExecutionStatusEnum.UNDO_FAIL);
        stepRun.setUndoneAt(LocalDateTime.now());
        stepRunRepository.save(stepRun);

        // Update orchestration run status to FAILED (UNDO failed)
        orchestrationRun.setStatus(ExecutionStatusEnum.FAILED);
        orchestrationRun.setCompletedAt(LocalDateTime.now());
        orchestrationRunRepository.save(orchestrationRun);

        log.error("UNDO process failed completely for flowId: {}", orchestrationRun.getFlowId());
    }

    /**
     * Handle sequential UNDO success - undo previous step (reverse order)
     */
    private void handleSequentialUndoSuccess(OrchestrationRun orchestrationRun, OrchestrationTemplate template,
                                            String completedStepName, ExecutionMessage message) {
        log.info("Handling sequential UNDO success: flowId={}, completedStep={}",
                orchestrationRun.getFlowId(), completedStepName);

        // Find next step to undo (in reverse order)
        List<OrchestrationStepTemplate> sortedSteps = template.getSteps().stream()
                .sorted(Comparator.comparing(OrchestrationStepTemplate::getSeq).reversed())
                .collect(Collectors.toList());

        // Find steps that need undo (DO_SUCCESS status)
        Optional<OrchestrationStepTemplate> nextStepOpt = sortedSteps.stream()
                .filter(step -> {
                    OrchestrationStepRun stepRun = orchestrationRun.getStepRuns().stream()
                            .filter(sr -> sr.getStepName().equals(step.getStepName()))
                            .findFirst()
                            .orElse(null);
                    return stepRun != null && stepRun.getStatus() == ExecutionStatusEnum.DO_SUCCESS;
                })
                .findFirst();

        if (nextStepOpt.isPresent()) {
            // Undo next step
            OrchestrationStepTemplate nextStep = nextStepOpt.get();
            log.info("Undoing next step: {}", nextStep.getStepName());

            // Update step status to UNDOING
            updateStepRunStatus(orchestrationRun.getFlowId(), nextStep.getStepName(), ExecutionStatusEnum.UNDOING);

            sendUndoMessage(orchestrationRun.getFlowId(), nextStep, message);
        } else {
            // All steps undone
            completeUndoProcess(orchestrationRun.getFlowId());
        }
    }

    /**
     * Handle parallel UNDO success - check if all steps undone
     */
    private void handleParallelUndoSuccess(OrchestrationRun orchestrationRun) {
        log.info("Handling parallel UNDO success: flowId={}", orchestrationRun.getFlowId());

        // Check if all DO_SUCCESS steps are now UNDO_SUCCESS
        boolean allUndone = orchestrationRun.getStepRuns().stream()
                .filter(stepRun -> stepRun.getStatus() == ExecutionStatusEnum.DO_SUCCESS ||
                                   stepRun.getStatus() == ExecutionStatusEnum.UNDO_SUCCESS ||
                                   stepRun.getStatus() == ExecutionStatusEnum.UNDOING)
                .allMatch(stepRun -> stepRun.getStatus() == ExecutionStatusEnum.UNDO_SUCCESS);

        if (allUndone) {
            completeUndoProcess(orchestrationRun.getFlowId());
        }
    }

    /**
     * Send UNDO message to worker
     */
    public void sendUndoMessage(String flowId, OrchestrationStepTemplate stepTemplate, ExecutionMessage message) {
        log.info("Sending UNDO message: flowId={}, stepName={}, topic={}",
                flowId, stepTemplate.getStepName(), stepTemplate.getUndoTopic());

        // Get orchestration name and worker service for audit
        Optional<OrchestrationRun> runOpt = orchestrationRunRepository.findByFlowId(flowId);
        if (runOpt.isPresent()) {
            String orchName = runOpt.get().getOrchName();
            Optional<OrchestrationStepRun> stepRunOpt = stepRunRepository
                    .findByOrchestrationRunFlowIdAndStepName(flowId, stepTemplate.getStepName());

            if (stepRunOpt.isPresent()) {
                String workerService = stepRunOpt.get().getWorkerService();

                // Record audit event for UNDO start
                auditService.recordUndoStart(flowId, orchName, stepTemplate.getStepName(), workerService);
            }
        }

        // Add step information to message headers
        Map<String, Object> headers = message.getHeaders();
        headers.put("flowId", flowId);
        headers.put("stepName", stepTemplate.getStepName());
        headers.put("action", "UNDO");
        headers.put("seq", stepTemplate.getSeq());

        try {
            messagePublisher.send(stepTemplate.getUndoTopic(), message);
            log.info("UNDO message sent successfully: flowId={}, stepName={}", flowId, stepTemplate.getStepName());
        } catch (Exception e) {
            log.error("Failed to send UNDO message: flowId={}, stepName={}", flowId, stepTemplate.getStepName(), e);

            // Update step status to UNDO_FAIL
            Optional<OrchestrationStepRun> stepRunOpt = stepRunRepository
                    .findByOrchestrationRunFlowIdAndStepName(flowId, stepTemplate.getStepName());

            if (stepRunOpt.isPresent()) {
                handleUndoFailure(orchestrationRunRepository.findByFlowId(flowId).get(),
                        stepRunOpt.get(), "Failed to send UNDO message: " + e.getMessage(), message);
            }
        }
    }

    /**
     * Start UNDO process for orchestration
     */
    @Transactional
    public void undoOrchestration(String flowId, ExecutionMessage message) {
        log.info("Starting UNDO process for orchestration: flowId={}", flowId);

        Optional<OrchestrationRun> runOpt = orchestrationRunRepository.findByFlowIdWithSteps(flowId);
        if (runOpt.isEmpty()) {
            log.error("Orchestration run not found for flowId: {}", flowId);
            return;
        }

        OrchestrationRun orchestrationRun = runOpt.get();

        // Update orchestration status to UNDOING
        orchestrationRun.setStatus(ExecutionStatusEnum.UNDOING);
        orchestrationRunRepository.save(orchestrationRun);

        // Get orchestration template
        Optional<OrchestrationTemplate> templateOpt = orchestrationTemplateRepository
                .findByOrchNameWithSteps(orchestrationRun.getOrchName());

        if (templateOpt.isEmpty()) {
            log.error("Orchestration template not found: {}", orchestrationRun.getOrchName());
            return;
        }

        OrchestrationTemplate template = templateOpt.get();

        // Find all successfully completed steps (DO_SUCCESS)
        List<OrchestrationStepRun> stepsToUndo = orchestrationRun.getStepRuns().stream()
                .filter(stepRun -> stepRun.getStatus() == ExecutionStatusEnum.DO_SUCCESS)
                .collect(Collectors.toList());

        // Record audit event for rollback start
        auditService.recordRollbackStarted(
            flowId,
            orchestrationRun.getOrchName(),
            "Starting rollback process for failed orchestration",
            stepsToUndo.size()
        );

        if (stepsToUndo.isEmpty()) {
            log.info("No steps to undo for flowId: {}", flowId);
            completeUndoProcess(flowId);
            return;
        }

        if (template.getType() == OrchestrationTypeEnum.SEQUENTIAL) {
            // Undo in reverse order - start with last successful step
            List<OrchestrationStepTemplate> sortedSteps = template.getSteps().stream()
                    .sorted(Comparator.comparing(OrchestrationStepTemplate::getSeq).reversed())
                    .collect(Collectors.toList());

            Optional<OrchestrationStepTemplate> firstToUndoOpt = sortedSteps.stream()
                    .filter(step -> stepsToUndo.stream()
                            .anyMatch(sr -> sr.getStepName().equals(step.getStepName())))
                    .findFirst();

            if (firstToUndoOpt.isPresent()) {
                OrchestrationStepTemplate firstToUndo = firstToUndoOpt.get();
                updateStepRunStatus(flowId, firstToUndo.getStepName(), ExecutionStatusEnum.UNDOING);
                sendUndoMessage(flowId, firstToUndo, message);
            }
        } else {
            // Undo all successful steps in parallel
            for (OrchestrationStepRun stepRun : stepsToUndo) {
                Optional<OrchestrationStepTemplate> stepTemplateOpt = template.getSteps().stream()
                        .filter(st -> st.getStepName().equals(stepRun.getStepName()))
                        .findFirst();

                if (stepTemplateOpt.isPresent()) {
                    updateStepRunStatus(flowId, stepRun.getStepName(), ExecutionStatusEnum.UNDOING);
                    sendUndoMessage(flowId, stepTemplateOpt.get(), message);
                }
            }
        }
    }

    /**
     * Update step run status
     */
    private void updateStepRunStatus(String flowId, String stepName, ExecutionStatusEnum status) {
        Optional<OrchestrationStepRun> stepRunOpt = stepRunRepository
                .findByOrchestrationRunFlowIdAndStepName(flowId, stepName);

        if (stepRunOpt.isPresent()) {
            OrchestrationStepRun stepRun = stepRunOpt.get();
            stepRun.setStatus(status);

            if (status == ExecutionStatusEnum.UNDOING) {
                stepRun.setRetryCount(0); // Reset retry count for undo
            }

            stepRunRepository.save(stepRun);
            log.info("Step run status updated: flowId={}, stepName={}, status={}", flowId, stepName, status);
        }
    }

    /**
     * Complete UNDO process
     */
    private void completeUndoProcess(String flowId) {
        log.info("Completing UNDO process: flowId={}", flowId);

        Optional<OrchestrationRun> runOpt = orchestrationRunRepository.findByFlowIdWithSteps(flowId);
        if (runOpt.isPresent()) {
            OrchestrationRun run = runOpt.get();

            // Count rolled back steps
            int rolledBackCount = (int) run.getStepRuns().stream()
                    .filter(sr -> sr.getStatus() == ExecutionStatusEnum.UNDO_SUCCESS)
                    .count();

            run.setStatus(ExecutionStatusEnum.UNDONE);
            run.setCompletedAt(LocalDateTime.now());
            orchestrationRunRepository.save(run);

            // Record audit event for rollback completion
            auditService.recordRollbackComplete(
                flowId,
                run.getOrchName(),
                rolledBackCount
            );

            log.info("Orchestration UNDO completed: flowId={}, rolledBackSteps={}", flowId, rolledBackCount);
        }
    }
}

