package com.ecom.orchestrator.service;

import com.ecom.orchestrator.constant.RegistrationConstants;
import com.ecom.orchestrator.dto.OrchestrationRegistrationDto;
import com.ecom.orchestrator.dto.RegistrationResult;
import com.ecom.orchestrator.entity.RegistrationRoleEnum;
import com.ecom.orchestrator.service.strategy.RegistrationStrategy;
import com.ecom.orchestrator.service.strategy.RegistrationStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Orchestration Registry Service - Simplified with Strategy Pattern
 * Uses strategies for different registration types and async operations for better performance
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrchestrationRegistryService {

    private final RegistrationStrategyFactory strategyFactory;
    private final OrchestrationStatusService orchestrationStatusService;
    private final RegistrationAsyncService asyncService;

    /**
     * Register orchestration using strategy pattern
     * Delegates to appropriate strategy based on role
     */
    @Transactional
    public void registerOrchestration(OrchestrationRegistrationDto registrationDto, String serviceName) {
        log.info("Registering orchestration: {} as: {} by service: {}",
                registrationDto.getOrchestrationName(), registrationDto.getAs(), serviceName);

        // Get appropriate strategy based on role
        RegistrationStrategy strategy = strategyFactory.getStrategy(registrationDto.getAs());

        // Execute registration using strategy
        RegistrationResult result = strategy.register(registrationDto, serviceName);

        // Update orchestration status if it's an initiator or successful worker registration
        if (RegistrationConstants.ROLE_INITIATOR.equalsIgnoreCase(registrationDto.getAs()) ||
            (RegistrationConstants.ROLE_WORKER.equalsIgnoreCase(registrationDto.getAs()) && !result.isFailed())) {
            orchestrationStatusService.updateOrchestrationStatus(registrationDto.getOrchestrationName());
        }

        // Determine role enum for audit
        RegistrationRoleEnum roleEnum = RegistrationConstants.ROLE_INITIATOR.equalsIgnoreCase(registrationDto.getAs())
                ? RegistrationRoleEnum.INITIATOR
                : RegistrationRoleEnum.WORKER;

        // Audit and publish asynchronously (non-blocking)
        asyncService.auditRegistrationAsync(
                registrationDto.getOrchestrationName(),
                roleEnum,
                serviceName,
                result.getStatus(),
                result.getSuccessfulSteps(),
                result.getFailedSteps()
        );

        asyncService.publishRegistrationStatusAsync(
                registrationDto.getOrchestrationName(),
                registrationDto.getAs(),
                serviceName,
                result.getStatus(),
                result.getFailedSteps()
        );

        log.info("Completed registration for: {} as: {} with status: {}",
                registrationDto.getOrchestrationName(), registrationDto.getAs(), result.getStatus());
    }
}
