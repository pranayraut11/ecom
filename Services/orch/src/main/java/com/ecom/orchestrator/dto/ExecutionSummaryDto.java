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
@Schema(description = "Summary information about an orchestration execution")
public class ExecutionSummaryDto {

    @Schema(description = "Unique execution identifier", example = "f14a9c8b-1234")
    private String executionId;

    @Schema(description = "Execution status", example = "SUCCESS", allowableValues = {"SUCCESS", "FAILED", "IN_PROGRESS", "ROLLED_BACK"})
    private String status;

    @Schema(description = "Execution start time", example = "2025-10-23T08:22:45.123Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime startTime;

    @Schema(description = "Execution end time", example = "2025-10-23T08:23:10.456Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime endTime;

    @Schema(description = "Service that initiated this execution", example = "initiator-service")
    private String initiator;

    @Schema(description = "Number of steps that executed successfully", example = "2")
    private Integer executedSteps;

    @Schema(description = "Number of steps that failed", example = "0")
    private Integer failedSteps;
}
