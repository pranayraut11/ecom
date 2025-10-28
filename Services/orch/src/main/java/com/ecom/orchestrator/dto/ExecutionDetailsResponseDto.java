package com.ecom.orchestrator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed execution information for a specific orchestration run including all step executions")
public class ExecutionDetailsResponseDto {

    @Schema(description = "Unique execution identifier", example = "f14a9c8b-1234")
    private String executionId;

    @Schema(description = "Orchestration name", example = "tenantCreation")
    private String orchName;

    @Schema(description = "Overall execution status", example = "SUCCESS", allowableValues = {"SUCCESS", "FAILED", "IN_PROGRESS", "ROLLED_BACK"})
    private String status;

    @Schema(description = "List of all step executions in this orchestration run, ordered by sequence")
    private List<StepExecutionDto> steps;
}
