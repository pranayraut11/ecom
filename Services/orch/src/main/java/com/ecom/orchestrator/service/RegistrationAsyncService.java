package com.ecom.orchestrator.service;

import com.ecom.orchestrator.constant.RegistrationConstants;
import com.ecom.orchestrator.dto.ExecutionMessage;
import com.ecom.orchestrator.dto.RegistrationStatusEventDto;
import com.ecom.orchestrator.dto.StepDefinitionDto;
import com.ecom.orchestrator.entity.RegistrationAudit;
import com.ecom.orchestrator.entity.RegistrationRoleEnum;
import com.ecom.orchestrator.entity.RegistrationStatusEnum;
import com.ecom.orchestrator.mapper.OrchestrationMapper;
import com.ecom.orchestrator.messaging.interfaces.MessagePublisher;
import com.ecom.orchestrator.repository.RegistrationAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to handle async operations like audit and event publishing
 * This improves response time by not blocking the main registration flow
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationAsyncService {

    private final RegistrationAuditRepository registrationAuditRepository;
    private final MessagePublisher messagePublisher;
    private final OrchestrationMapper orchestrationMapper;

    /**
     * Audit registration asynchronously
     * This doesn't block the main registration flow
     */
    @Async
    public void auditRegistrationAsync(String orchName, RegistrationRoleEnum role, String serviceName,
                                       RegistrationStatusEnum status, List<StepDefinitionDto> steps,
                                       List<String> failedSteps) {
        try {
            log.info("Async auditing registration for: {} as: {} with status: {}", orchName, role, status);
            
            RegistrationAudit audit = orchestrationMapper.toRegistrationAudit(
                    orchName, role, serviceName, status, steps, failedSteps);
            registrationAuditRepository.save(audit);
            
            log.info("Successfully audited registration for: {}", orchName);
        } catch (Exception e) {
            log.error("Error auditing registration asynchronously for: {}", orchName, e);
        }
    }

    /**
     * Publish registration status asynchronously
     * This doesn't block the main registration flow
     */
    @Async
    public void publishRegistrationStatusAsync(String orchName, String asRole, String serviceName,
                                               RegistrationStatusEnum status, List<String> failedSteps) {
        try {
            log.info("Async publishing registration status for: {} as: {} with status: {}", orchName, asRole, status);
            
            RegistrationStatusEventDto event = orchestrationMapper.toRegistrationStatusEvent(
                    orchName, asRole, serviceName, status, failedSteps);

            ExecutionMessage message = orchestrationMapper.toExecutionMessage(
                    event, orchName, asRole, serviceName, status);

            messagePublisher.send(RegistrationConstants.TOPIC_REGISTRATION_STATUS, message);

            log.info("Successfully published registration status event for: {} as: {} status: {}",
                    orchName, asRole, status);
        } catch (Exception e) {
            log.error("Error publishing registration status asynchronously for: {}", orchName, e);
        }
    }
}

