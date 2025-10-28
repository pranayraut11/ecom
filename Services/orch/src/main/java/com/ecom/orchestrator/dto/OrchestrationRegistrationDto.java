package com.ecom.orchestrator.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrchestrationRegistrationDto {
    @NotEmpty
    private String orchestrationName;
    @NotEmpty
    private String as; // "initiator" or "worker"
    @NotEmpty
    private String type; // "sequential" or "simultaneous" (only for initiator)
    @NotEmpty
    @Valid
    private List<StepDefinitionDto> steps;
}
