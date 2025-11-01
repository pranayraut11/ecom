package com.ecom.orchestrator.dto;

import com.ecom.orchestrator.entity.RegistrationStatusEnum;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Value object representing the result of a registration operation
 */
@Value
@Builder
public class RegistrationResult {
    RegistrationStatusEnum status;
    @Builder.Default
    List<String> failedSteps = new ArrayList<>();
    @Builder.Default
    List<StepDefinitionDto> successfulSteps = new ArrayList<>();

    /**
     * Check if registration was successful
     */
    public boolean isSuccess() {
        return status == RegistrationStatusEnum.SUCCESS ||
               status == RegistrationStatusEnum.PENDING;
    }

    /**
     * Check if registration failed
     */
    public boolean isFailed() {
        return status == RegistrationStatusEnum.FAILED;
    }
}

