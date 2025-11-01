package com.ecom.orchestrator.service.strategy;

import com.ecom.orchestrator.dto.OrchestrationRegistrationDto;
import com.ecom.orchestrator.dto.RegistrationResult;

/**
 * Strategy interface for different registration types
 */
public interface RegistrationStrategy {

    /**
     * Execute the registration logic
     *
     * @param registrationDto Registration details
     * @param serviceName Service requesting registration
     * @return Result of the registration operation
     */
    RegistrationResult register(OrchestrationRegistrationDto registrationDto, String serviceName);

    /**
     * Get the role this strategy handles
     */
    String getRole();
}

