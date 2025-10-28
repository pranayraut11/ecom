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

    @Schema(description = "Step sequence number", example = "1")
    private Integer seq;

    @Schema(description = "Step name", example = "createRealm")
    private String name;

    @Schema(description = "Step execution status", example = "SUCCESS", allowableValues = {"SUCCESS", "FAILED", "IN_PROGRESS", "ROLLED_BACK"})
    private String status;

    @Schema(description = "Step execution start time", example = "2025-10-23T08:22:46Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime startTime;

    @Schema(description = "Step execution end time", example = "2025-10-23T08:22:47Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime endTime;

    @Schema(description = "Step execution duration in milliseconds", example = "1000")
    private Long durationMs;

    @Schema(description = "Worker service that executed this step", example = "worker-service-A")
    private String workerService;

    @Schema(description = "Error message if step failed")
    private String errorMessage;
}
