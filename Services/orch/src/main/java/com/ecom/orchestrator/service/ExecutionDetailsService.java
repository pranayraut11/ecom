package com.ecom.orchestrator.service;

import com.ecom.orchestrator.dto.ExecutionDetailsResponseDto;
import com.ecom.orchestrator.dto.StepExecutionDto;
import com.ecom.orchestrator.entity.ExecutionStatusEnum;
import com.ecom.orchestrator.entity.OrchestrationRun;
import com.ecom.orchestrator.entity.OrchestrationStepRun;
import com.ecom.orchestrator.repository.OrchestrationRunRepository;
import com.ecom.orchestrator.repository.OrchestrationTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExecutionDetailsService {

    private final OrchestrationRunRepository orchestrationRunRepository;
    private final OrchestrationTemplateRepository orchestrationTemplateRepository;

    public ExecutionDetailsResponseDto getExecutionDetails(String orchName, String executionId) {
        log.info("Fetching execution details for orchestration: {} with executionId: {}", orchName, executionId);

        // Verify orchestration exists
        if (!orchestrationTemplateRepository.existsByOrchName(orchName)) {
            log.warn("Orchestration not found: {}", orchName);
            return null;
        }

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
                .collect(Collectors.toList());

        log.info("Retrieved execution details for: {} with {} steps", executionId, stepExecutions.size());

        return ExecutionDetailsResponseDto.builder()
                .executionId(execution.getFlowId())
                .orchName(execution.getOrchName())
                .status(mapStatusToApi(execution.getStatus()))
                .steps(stepExecutions)
                .build();
    }

    private StepExecutionDto convertToStepExecutionDto(OrchestrationStepRun stepRun) {
        // Calculate duration in milliseconds
        Long durationMs = null;
        if (stepRun.getStartedAt() != null && stepRun.getCompletedAt() != null) {
            durationMs = Duration.between(stepRun.getStartedAt(), stepRun.getCompletedAt()).toMillis();
        }

        return StepExecutionDto.builder()
                .seq(stepRun.getSeq())
                .name(stepRun.getStepName())
                .status(mapStatusToApi(stepRun.getStatus()))
                .startTime(stepRun.getStartedAt())
                .endTime(stepRun.getCompletedAt())
                .durationMs(durationMs)
                .workerService(stepRun.getWorkerService())
                .errorMessage(stepRun.getErrorMessage())
                .build();
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
        };
    }
}

