package com.ecom.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Detailed information about a step execution within an orchestration run")
public class StepExecutionDto {

    // Core Identifiers
    @Schema(description = "Step sequence number", example = "1")
    private Integer seq;

    @Schema(description = "Step name", example = "createRealm")
    private String name;

    @Schema(description = "Step execution status", example = "SUCCESS", allowableValues = {"SUCCESS", "FAILED", "IN_PROGRESS", "ROLLED_BACK"})
    private String status;

    @Schema(description = "Operation type", example = "DO", allowableValues = {"DO", "UNDO"})
    private String operationType;

    // Execution Information
    @Schema(description = "Worker service that executed this step", example = "worker-realm-service")
    private String executedBy;

    @Schema(description = "Step execution start time", example = "2025-10-23T08:22:46.123Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime startTime;

    @Schema(description = "Step execution end time", example = "2025-10-23T08:22:47.456Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime endTime;

    @Schema(description = "Step execution duration in milliseconds", example = "1000")
    private Long durationMs;

    // Error and Failure Information
    @Schema(description = "Error message if step failed")
    private String errorMessage;

    @Schema(description = "Detailed failure reason", example = "Client already exists")
    private String failureReason;

    // Retry Information
    @Schema(description = "Current retry count", example = "2")
    private Integer retryCount;

    @Schema(description = "Maximum retries allowed", example = "3")
    private Integer maxRetries;

    @Schema(description = "Last retry attempt timestamp", example = "2025-10-23T08:22:50.789Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime lastRetryAt;

    // Rollback Information
    @Schema(description = "Whether rollback was triggered for this step", example = "true")
    private Boolean rollbackTriggered;

    @Schema(description = "Reference to the rollback step (UNDO operation)", example = "undoCreateRealm")
    private String rollbackStepRef;

    // Additional Information
    @Schema(description = "URL to external logs for this step", example = "https://logs.example.com/step/123")
    private String logsUrl;

    // Deprecated field - kept for backward compatibility
    @Schema(description = "Worker service that executed this step (deprecated, use executedBy)", example = "worker-service-A", deprecated = true)
    private String workerService;
}
