package com.ecom.orchestrator.service;

import com.ecom.orchestrator.entity.OrchestrationStatusEnum;
import com.ecom.orchestrator.entity.OrchestrationStepTemplate;
import com.ecom.orchestrator.entity.OrchestrationTemplate;
import com.ecom.orchestrator.repository.OrchestrationStepTemplateRepository;
import com.ecom.orchestrator.repository.OrchestrationTemplateRepository;
import com.ecom.orchestrator.repository.WorkerRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service to handle orchestration status management
 * Extracted from OrchestrationRegistryService for better separation of concerns
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrchestrationStatusService {

    private final OrchestrationTemplateRepository orchestrationTemplateRepository;
    private final OrchestrationStepTemplateRepository stepTemplateRepository;
    private final WorkerRegistrationRepository workerRegistrationRepository;

    /**
     * Update orchestration status based on worker registrations
     * Optimized with single query instead of N+1
     */
    public void updateOrchestrationStatus(String orchName) {
        OrchestrationTemplate template = orchestrationTemplateRepository
                .findByOrchNameWithSteps(orchName)
                .orElseThrow(() -> {
                    log.warn("Orchestration template not found: {}", orchName);
                    return new IllegalArgumentException("Orchestration template not found: " + orchName);
                });

        List<OrchestrationStepTemplate> steps = initializeSteps(template, orchName);
        if (steps.isEmpty()) {
            log.warn("No steps found for orchestration: {}", orchName);
            return;
        }

        // Optimization: Use single query instead of N queries (fixes N+1 problem)
        long registeredStepsCount = workerRegistrationRepository.countDistinctStepsByOrchName(orchName);
        boolean allStepsHaveWorkers = registeredStepsCount == steps.size();

        if (allStepsHaveWorkers && template.getStatus() != OrchestrationStatusEnum.SUCCESS) {
            template.setStatus(OrchestrationStatusEnum.SUCCESS);
            template.setFailureReason(null);
            orchestrationTemplateRepository.save(template);
            log.info("Orchestration status updated to SUCCESS: {}", orchName);
        } else if (!allStepsHaveWorkers) {
            log.debug("Not all steps have workers registered for orchestration: {} ({}/{})",
                    orchName, registeredStepsCount, steps.size());
        }
    }

    /**
     * Initialize and return the steps collection, handling potential lazy loading issues
     */
    private List<OrchestrationStepTemplate> initializeSteps(OrchestrationTemplate template, String orchName) {
        try {
            List<OrchestrationStepTemplate> steps = template.getSteps();

            if (steps == null || steps.isEmpty()) {
                log.debug("Steps collection is null/empty, fetching directly from repository for: {}", orchName);
                steps = stepTemplateRepository.findByTemplateOrchNameOrderBySeq(orchName);
            }

            if (steps != null) {
                steps.size(); // Trigger lazy loading if needed
            }

            return steps != null ? steps : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error initializing steps for orchestration: {}", orchName, e);
            return stepTemplateRepository.findByTemplateOrchNameOrderBySeq(orchName);
        }
    }
}

