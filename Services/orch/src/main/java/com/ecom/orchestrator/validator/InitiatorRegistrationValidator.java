package com.ecom.orchestrator.validator;

import com.ecom.orchestrator.dto.StepDefinitionDto;
import com.ecom.orchestrator.repository.OrchestrationStepTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Validator for initiator registration requests
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InitiatorRegistrationValidator {

    private final OrchestrationStepTemplateRepository stepTemplateRepository;

    /**
     * Validate step definitions for initiator registration
     * 
     * @return Map of step names to validation error messages (empty if all valid)
     */
    public Map<String, String> validateSteps(List<StepDefinitionDto> steps, String orchName) {
        Map<String, String> validationErrors = new HashMap<>();
        Set<String> processedStepNames = new HashSet<>();
        Set<Integer> processedSequences = new HashSet<>();

        for (StepDefinitionDto step : steps) {
            // Check for duplicate step names
            if (processedStepNames.contains(step.getName())) {
                validationErrors.put(step.getName(), "Duplicate step name in request: " + step.getName());
                log.warn("Duplicate step name found in registration request: {} for orchestration: {}", 
                        step.getName(), orchName);
                continue;
            }

            // Check for duplicate sequence numbers
            if (processedSequences.contains(step.getSeq())) {
                validationErrors.put(step.getName(), 
                        "Duplicate sequence number " + step.getSeq() + " for step: " + step.getName());
                log.warn("Duplicate sequence number found in registration request: {} for step: {} in orchestration: {}",
                        step.getSeq(), step.getName(), orchName);
                continue;
            }

            // Check if step template already exists (safety check)
            if (stepTemplateRepository.existsByTemplateOrchNameAndStepName(orchName, step.getName())) {
                validationErrors.put(step.getName(), "Step template already exists: " + step.getName());
                log.warn("Step template already exists: {} for orchestration: {}", step.getName(), orchName);
                continue;
            }

            processedStepNames.add(step.getName());
            processedSequences.add(step.getSeq());
        }

        return validationErrors;
    }

    /**
     * Check if all validations passed
     */
    public boolean isValid(Map<String, String> validationErrors) {
        return validationErrors.isEmpty();
    }
}

