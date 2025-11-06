package com.ecom.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Detailed execution information for a specific orchestration run including all step executions")
public class ExecutionDetailsResponseDto {

    // Core Identifiers
    @Schema(description = "Unique execution identifier", example = "f14a9c8b-1234")
    private String executionId;

    @Schema(description = "Orchestration name", example = "tenantCreation")
    private String orchName;

    @Schema(description = "Overall execution status", example = "SUCCESS", allowableValues = {"SUCCESS", "FAILED", "IN_PROGRESS", "ROLLED_BACK"})
    private String status;

    @Schema(description = "Orchestration type", example = "SEQUENTIAL", allowableValues = {"SEQUENTIAL", "PARALLEL"})
    private String type;

    // Initiator Information
    @Schema(description = "Service that initiated this orchestration execution", example = "tenant-management-service")
    private String initiator;

    @Schema(description = "How execution was triggered", example = "USER", allowableValues = {"USER", "SYSTEM", "SCHEDULED"})
    @Builder.Default
    private String triggeredBy = "USER";

    @Schema(description = "Correlation ID for tracing", example = "tenant-xyz-2025-11-03")
    private String correlationId;

    // Timing Information
    @Schema(description = "Execution start time", example = "2025-11-03T10:22:45.123")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime startedAt;

    @Schema(description = "Execution completion time", example = "2025-11-03T10:23:10.456")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime completedAt;

    @Schema(description = "Last updated timestamp", example = "2025-11-03T10:23:10.789")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime lastUpdatedAt;

    @Schema(description = "Overall execution duration in milliseconds", example = "16000")
    private Long overallDurationMs;

    // Step Statistics
    @Schema(description = "Total number of steps", example = "5")
    private Integer totalSteps;

    @Schema(description = "Number of successful steps", example = "3")
    private Integer successfulSteps;

    @Schema(description = "Number of failed steps", example = "1")
    private Integer failedSteps;

    @Schema(description = "Number of rolled back steps", example = "1")
    private Integer rolledBackSteps;

    @Schema(description = "Execution completion percentage", example = "60.0")
    private Double percentageCompleted;

    // Retry Policy
    @Schema(description = "Retry policy configuration")
    private RetryPolicyDto retryPolicy;

    // Step Details
    @Schema(description = "List of all step executions in this orchestration run, ordered by sequence")
    private List<StepExecutionDto> steps;

    // Timeline Events (Optional)
    @Schema(description = "Detailed timeline of execution events")
    private List<TimelineEventDto> timeline;
}
