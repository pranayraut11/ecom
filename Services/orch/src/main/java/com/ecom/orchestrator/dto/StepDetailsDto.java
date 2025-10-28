package com.ecom.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Detailed information about a registered step in an orchestration")
public class StepDetailsDto {

    @Schema(description = "Step sequence number", example = "1")
    private Integer seq;

    @Schema(description = "Step name", example = "createRealm")
    private String name;

    @Schema(description = "Object type for the step", example = "String")
    private String objectType;

    @Schema(description = "Service that registered this step", example = "worker-service-A")
    private String registeredBy;

    @Schema(description = "Registration status of the step", example = "SUCCESS", allowableValues = {"SUCCESS", "FAILED", "PENDING"})
    private String status;

    @Schema(description = "Failure reason if registration failed", example = "Step already exists")
    private String failureReason;
}
