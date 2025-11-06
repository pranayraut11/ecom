package com.ecom.orchestrator.service;

import com.ecom.orchestrator.dto.ExecutionDetailsResponseDto;
import com.ecom.orchestrator.dto.RetryPolicyDto;
import com.ecom.orchestrator.dto.StepExecutionDto;
import com.ecom.orchestrator.dto.TimelineEventDto;
import com.ecom.orchestrator.entity.*;
import com.ecom.orchestrator.repository.OrchestrationRunRepository;
import com.ecom.orchestrator.repository.OrchestrationTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExecutionDetailsService {

    private final OrchestrationRunRepository orchestrationRunRepository;
    private final OrchestrationTemplateRepository orchestrationTemplateRepository;

    public ExecutionDetailsResponseDto getExecutionDetails(String orchName, String executionId) {
        log.info("Fetching execution details for orchestration: {} with executionId: {}", orchName, executionId);

        // Verify orchestration exists and get template to retrieve initiator and type
        Optional<OrchestrationTemplate> templateOpt =
                orchestrationTemplateRepository.findByOrchName(orchName);

        if (templateOpt.isEmpty()) {
            log.warn("Orchestration not found: {}", orchName);
            return null;
        }

        OrchestrationTemplate template = templateOpt.get();
        String initiatorService = template.getInitiatorService();
        String orchestrationType = template.getType().name();

        // Find the specific execution with step data
        Optional<OrchestrationRun> executionOpt = orchestrationRunRepository.findByFlowIdWithSteps(executionId);

        if (executionOpt.isEmpty()) {
            log.warn("Execution not found: {}", executionId);
            return null;
        }

        OrchestrationRun execution = executionOpt.get();

        // Verify execution belongs to the specified orchestration
        if (!orchName.equals(execution.getOrchName())) {
            log.warn("Execution {} does not belong to orchestration {}", executionId, orchName);
            return null;
        }

        // Build step execution details
        List<StepExecutionDto> stepExecutions = execution.getStepRuns().stream()
                .sorted(Comparator.comparingInt(OrchestrationStepRun::getSeq))
                .map(this::convertToStepExecutionDto)
                .toList();

        // Calculate statistics
        int totalSteps = stepExecutions.size();
        int successfulSteps = (int) stepExecutions.stream()
                .filter(step -> "SUCCESS".equals(step.getStatus()))
                .count();
        int failedSteps = (int) stepExecutions.stream()
                .filter(step -> "FAILED".equals(step.getStatus()))
                .count();
        int rolledBackSteps = (int) stepExecutions.stream()
                .filter(step -> "ROLLED_BACK".equals(step.getStatus()))
                .count();

        // Calculate overall duration
        Long overallDurationMs = null;
        if (execution.getStartedAt() != null && execution.getCompletedAt() != null) {
            overallDurationMs = Duration.between(execution.getStartedAt(), execution.getCompletedAt()).toMillis();
        }

        // Calculate percentage completed
        Double percentageCompleted = totalSteps > 0 ? ((double) successfulSteps / totalSteps) * 100 : 0.0;

        // Build retry policy (using default values, can be made configurable)
        RetryPolicyDto retryPolicy = RetryPolicyDto.builder()
                .maxRetries(3)  // Default, can be made configurable
                .backoffMs(5000L)  // Default 5 seconds
                .build();

        // Build timeline events (now delegated to timeline API, return empty for backward compatibility)
        List<TimelineEventDto> timeline = Collections.emptyList();

        log.info("Retrieved execution details for: {} with {} steps", executionId, stepExecutions.size());

        return ExecutionDetailsResponseDto.builder()
                .executionId(execution.getFlowId())
                .orchName(execution.getOrchName())
                .status(mapStatusToApi(execution.getStatus()))
                .type(orchestrationType)
                .initiator(initiatorService)
                .triggeredBy(execution.getTriggeredBy())
                .correlationId(execution.getCorrelationId())
                .startedAt(execution.getStartedAt())
                .completedAt(execution.getCompletedAt())
                .lastUpdatedAt(execution.getLastUpdatedAt())
                .overallDurationMs(overallDurationMs)
                .totalSteps(totalSteps)
                .successfulSteps(successfulSteps)
                .failedSteps(failedSteps)
                .rolledBackSteps(rolledBackSteps)
                .percentageCompleted(percentageCompleted)
                .retryPolicy(retryPolicy)
                .steps(stepExecutions)
                .timeline(timeline)
                .build();
    }

    private StepExecutionDto convertToStepExecutionDto(OrchestrationStepRun stepRun) {
        // Calculate duration in milliseconds
        Long durationMs = null;
        if (stepRun.getStartedAt() != null && stepRun.getCompletedAt() != null) {
            durationMs = Duration.between(stepRun.getStartedAt(), stepRun.getCompletedAt()).toMillis();
        }

        // Determine operation type based on status if not explicitly set
        String operationType = stepRun.getOperationType();
        if (operationType == null) {
            operationType = isUndoOperation(stepRun.getStatus()) ? "UNDO" : "DO";
        }

        // Determine rollback step reference if applicable
        String rollbackStepRef = null;
        if (Boolean.TRUE.equals(stepRun.getRollbackTriggered())) {
            rollbackStepRef = "undo" + capitalize(stepRun.getStepName());
        }

        return StepExecutionDto.builder()
                .seq(stepRun.getSeq())
                .name(stepRun.getStepName())
                .status(mapStatusToApi(stepRun.getStatus()))
                .operationType(operationType)
                .executedBy(stepRun.getWorkerService())
                .startTime(stepRun.getStartedAt())
                .endTime(stepRun.getCompletedAt())
                .durationMs(durationMs)
                .errorMessage(stepRun.getErrorMessage())
                .failureReason(stepRun.getFailureReason())
                .retryCount(stepRun.getRetryCount())
                .maxRetries(stepRun.getMaxRetries())
                .lastRetryAt(stepRun.getLastRetryAt())
                .rollbackTriggered(stepRun.getRollbackTriggered())
                .rollbackStepRef(rollbackStepRef)
                .workerService(stepRun.getWorkerService())  // Backward compatibility
                .build();
    }

    private boolean isUndoOperation(ExecutionStatusEnum status) {
        return status == ExecutionStatusEnum.UNDOING ||
               status == ExecutionStatusEnum.UNDO_SUCCESS ||
               status == ExecutionStatusEnum.UNDO_FAIL ||
               status == ExecutionStatusEnum.UNDONE;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String mapStatusToApi(ExecutionStatusEnum status) {
        return switch (status) {
            case COMPLETED -> "SUCCESS";
            case FAILED -> "FAILED";
            case IN_PROGRESS -> "IN_PROGRESS";
            case UNDONE -> "ROLLED_BACK";
            case PENDING -> "IN_PROGRESS";
            case UNDOING -> "IN_PROGRESS";
            case NOT_REGISTERED -> "NOT_REGISTERED";
            case DO_SUCCESS -> "SUCCESS";
            case DO_FAIL -> "FAILED";
            case UNDO_SUCCESS -> "ROLLED_BACK";
            case UNDO_FAIL -> "FAILED";
            case RETRY_EXHAUSTED -> "FAILED";
        };
    }
}

