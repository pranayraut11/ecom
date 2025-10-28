package com.ecom.orchestrator.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StepDefinitionDto {
    private Integer seq; // Only for initiator
    @NotEmpty
    private String name;
    @NotEmpty
    private String objectType;

    private String handlerClass;

    private String handlerMethod;
}
