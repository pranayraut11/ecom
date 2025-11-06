package com.ecom.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Schema(description = "Detailed registration information for a specific orchestration")
public class OrchestrationDetailsResponseDto {

    @Schema(description = "Orchestration name", example = "tenantCreation")
    private String orchName;

    @Schema(description = "Orchestration type", example = "SEQUENTIAL", allowableValues = {"SEQUENTIAL", "SIMULTANEOUS"})
    private String type;

    @Schema(description = "Overall orchestration status", example = "FAILED", allowableValues = {"REGISTERED", "PARTIALLY_REGISTERED", "FAILED"})
    private String status;

    @Schema(description = "Service that initiated this orchestration", example = "tenant-management-service")
    private String initiator;

    @Schema(description = "Orchestration registration time", example = "2025-11-03T10:22:45.123")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    @Schema(description = "List of all steps in the orchestration with their registration details")
    private List<StepDetailsDto> steps;
}
