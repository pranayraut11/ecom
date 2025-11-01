package com.ecom.orchestrator.dto;

import lombok.Builder;
import lombok.Value;

/**
 * Value object representing the context of a registration operation
 */
@Value
@Builder
public class RegistrationContext {
    String orchName;
    String serviceName;
    String role;
    OrchestrationRegistrationDto registrationDto;
}

