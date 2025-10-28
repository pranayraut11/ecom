package com.ecom.orchestrator.service;

import com.ecom.orchestrator.dto.ExecutionHistoryRequestDto;
import com.ecom.orchestrator.dto.ExecutionSummaryDto;
import com.ecom.orchestrator.dto.PagedExecutionHistoryResponseDto;
import com.ecom.orchestrator.entity.ExecutionStatusEnum;
import com.ecom.orchestrator.entity.OrchestrationRun;
import com.ecom.orchestrator.entity.OrchestrationStepRun;
import com.ecom.orchestrator.entity.OrchestrationTemplate;
import com.ecom.orchestrator.repository.OrchestrationRunRepository;
import com.ecom.orchestrator.repository.OrchestrationTemplateRepository;
import com.ecom.orchestrator.specification.ExecutionHistorySpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExecutionHistoryService {

    private final OrchestrationRunRepository orchestrationRunRepository;
    private final OrchestrationTemplateRepository orchestrationTemplateRepository;

    public PagedExecutionHistoryResponseDto getExecutionHistory(String orchName, ExecutionHistoryRequestDto request) {
        log.info("Fetching execution history for orchestration: {} with filters - page: {}, size: {}, status: {}",
                orchName, request.getPage(), request.getSize(), request.getStatus());

        // Verify orchestration exists
        if (!orchestrationTemplateRepository.existsByOrchName(orchName)) {
            log.warn("Orchestration not found: {}", orchName);
            return null; // This will be handled by controller to return 404
        }

        // Create sort object
        Sort sort = createSort(request.getSortBy(), request.getDirection());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        // Build dynamic specification
        Specification<OrchestrationRun> spec = buildSpecification(orchName, request);

        // Execute query
        Page<OrchestrationRun> executionPage = orchestrationRunRepository.findAll(spec, pageable);

        // Convert to DTOs
        List<ExecutionSummaryDto> content = executionPage.getContent()
                .stream()
                .map(this::convertToExecutionSummary)
                .collect(Collectors.toList());

        log.info("Retrieved {} executions out of {} total for orchestration: {}",
                content.size(), executionPage.getTotalElements(), orchName);

        return PagedExecutionHistoryResponseDto.builder()
                .content(content)
                .page(request.getPage())
                .size(request.getSize())
                .totalElements(executionPage.getTotalElements())
                .totalPages(executionPage.getTotalPages())
                .build();
    }

    private Sort createSort(String sortBy, String direction) {
        // Default sorting by startTime DESC
        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "startTime";
        }

        // Validate sort direction
        Sort.Direction sortDirection = Sort.Direction.DESC;
        if ("asc".equalsIgnoreCase(direction)) {
            sortDirection = Sort.Direction.ASC;
        }

        // Map API field names to entity field names
        String entityFieldName = mapToEntityField(sortBy);

        return Sort.by(sortDirection, entityFieldName);
    }

    private String mapToEntityField(String apiFieldName) {
        return switch (apiFieldName.toLowerCase()) {
            case "starttime" -> "startedAt";
            case "endtime" -> "completedAt";
            case "status" -> "status";
            case "executionid" -> "flowId";
            default -> "startedAt"; // Default to startTime
        };
    }

    private Specification<OrchestrationRun> buildSpecification(String orchName, ExecutionHistoryRequestDto request) {
        Specification<OrchestrationRun> spec = Specification.where(null);

        // Always filter by orchestration name
        spec = spec.and(ExecutionHistorySpecifications.hasOrchName(orchName));

        // Status filter
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            try {
                ExecutionStatusEnum statusEnum = mapApiStatusToEnum(request.getStatus());
                spec = spec.and(ExecutionHistorySpecifications.hasStatus(statusEnum));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status filter: {}", request.getStatus());
            }
        }

        // Date range filters
        if (request.getFromDate() != null) {
            spec = spec.and(ExecutionHistorySpecifications.startedAfter(request.getFromDate()));
        }

        if (request.getToDate() != null) {
            spec = spec.and(ExecutionHistorySpecifications.startedBefore(request.getToDate()));
        }

        return spec;
    }

    private ExecutionStatusEnum mapApiStatusToEnum(String apiStatus) {
        return switch (apiStatus.toUpperCase()) {
            case "SUCCESS" -> ExecutionStatusEnum.COMPLETED;
            case "FAILED" -> ExecutionStatusEnum.FAILED;
            case "IN_PROGRESS" -> ExecutionStatusEnum.IN_PROGRESS;
            case "ROLLED_BACK" -> ExecutionStatusEnum.UNDONE;
            default -> throw new IllegalArgumentException("Invalid status: " + apiStatus);
        };
    }

    private String mapEnumToApiStatus(ExecutionStatusEnum status) {
        return switch (status) {
            case COMPLETED -> "SUCCESS";
            case FAILED -> "FAILED";
            case IN_PROGRESS -> "IN_PROGRESS";
            case UNDONE -> "ROLLED_BACK";
            case PENDING -> "IN_PROGRESS"; // Map PENDING to IN_PROGRESS for API consistency
            case UNDOING -> "IN_PROGRESS"; // Map UNDOING to IN_PROGRESS for API consistency
            case NOT_REGISTERED -> "NOT_REGISTERED";
        };
    }

    private ExecutionSummaryDto convertToExecutionSummary(OrchestrationRun run) {
        // Get initiator from orchestration template
        String initiator = orchestrationTemplateRepository.findByOrchName(run.getOrchName())
                .map(OrchestrationTemplate::getInitiatorService)
                .orElse("unknown");

        // Calculate step statistics
        int executedSteps = 0;
        int failedSteps = 0;

        if (run.getStepRuns() != null) {
            for (OrchestrationStepRun stepRun : run.getStepRuns()) {
                if (stepRun.getStatus() == ExecutionStatusEnum.COMPLETED) {
                    executedSteps++;
                } else if (stepRun.getStatus() == ExecutionStatusEnum.FAILED) {
                    failedSteps++;
                }
            }
        }

        return ExecutionSummaryDto.builder()
                .executionId(run.getFlowId())
                .status(mapEnumToApiStatus(run.getStatus()))
                .startTime(run.getStartedAt())
                .endTime(run.getCompletedAt())
                .initiator(initiator)
                .executedSteps(executedSteps)
                .failedSteps(failedSteps)
                .build();
    }
}
