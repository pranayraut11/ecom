package com.ecom.orchestrator.validator;

import com.ecom.orchestrator.dto.StepDefinitionDto;
import com.ecom.orchestrator.entity.OrchestrationStepTemplate;
import com.ecom.orchestrator.entity.OrchestrationTemplate;
import com.ecom.orchestrator.repository.WorkerRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Validator for worker registration requests
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WorkerRegistrationValidator {

    private final WorkerRegistrationRepository workerRegistrationRepository;

    /**
     * Validate step definitions for worker registration
     *
     * @return Map of step names to validation error messages (empty if all valid)
     */
    public Map<String, String> validateSteps(List<StepDefinitionDto> steps,
                                             OrchestrationTemplate template,
                                             String serviceName,
                                             String orchName) {
        Map<String, String> validationErrors = new HashMap<>();
        Set<String> processedStepNames = new HashSet<>();

        for (StepDefinitionDto step : steps) {
            // Check for duplicate step names in request
            if (processedStepNames.contains(step.getName())) {
                validationErrors.put(step.getName(), "Duplicate step name in request: " + step.getName());
                log.warn("Duplicate step name found in worker registration request: {} for orchestration: {}",
                        step.getName(), orchName);
                continue;
            }

            // Check if step exists in orchestration template
            boolean stepExistsInTemplate = template.getSteps().stream()
                    .anyMatch(st -> st.getStepName().equals(step.getName()));

            if (!stepExistsInTemplate) {
                validationErrors.put(step.getName(), "Step not found in orchestration: " + step.getName());
                log.warn("Step not found in orchestration template: {} for orchestration: {}",
                        step.getName(), orchName);
                continue;
            }

            // Check if worker is already registered (after deletion, this should not happen)
            if (workerRegistrationRepository.existsByOrchNameAndStepNameAndWorkerService(
                    orchName, step.getName(), serviceName)) {
                validationErrors.put(step.getName(), "Worker already registered for step: " + step.getName());
                log.warn("Worker already registered for step: {} in orchestration: {} by service: {}",
                        step.getName(), orchName, serviceName);
                continue;
            }

            processedStepNames.add(step.getName());
        }

        return validationErrors;
    }

    /**
     * Find step template by name
     */
    public Optional<OrchestrationStepTemplate> findStepTemplate(OrchestrationTemplate template, String stepName) {
        return template.getSteps().stream()
                .filter(st -> st.getStepName().equals(stepName))
                .findFirst();
    }

    /**
     * Check if all validations passed
     */
    public boolean isValid(Map<String, String> validationErrors) {
        return validationErrors.isEmpty();
    }
}

