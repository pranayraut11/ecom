package com.ecom.orchestrator.service;

import com.ecom.orchestrator.dto.StepDefinitionDto;
import com.ecom.orchestrator.entity.WorkerRegistration;
import com.ecom.orchestrator.mapper.OrchestrationMapper;
import com.ecom.orchestrator.repository.WorkerRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Helper service to handle worker registration operations
 * Consolidates duplicate logic for registering workers
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerRegistrationHelper {

    private final WorkerRegistrationRepository workerRegistrationRepository;
    private final OrchestrationMapper orchestrationMapper;

    /**
     * Register workers for given steps in batch
     * Handles both cases: when template exists and when it doesn't
     *
     * @param orchName Orchestration name
     * @param serviceName Worker service name
     * @param steps Steps to register
     * @param topicNameResolver Function to get topic name for a step
     * @return List of saved worker registrations
     */
    public List<WorkerRegistration> registerWorkersInBatch(
            String orchName,
            String serviceName,
            List<StepDefinitionDto> steps,
            Function<String, String> topicNameResolver) {

        List<WorkerRegistration> registrationsToSave = new ArrayList<>();

        for (StepDefinitionDto step : steps) {
            String topicName = topicNameResolver.apply(step.getName());

            WorkerRegistration registration = orchestrationMapper.toWorkerRegistration(
                    orchName,
                    step.getName(),
                    serviceName,
                    topicName);

            registrationsToSave.add(registration);
        }

        // Batch save all worker registrations
        if (!registrationsToSave.isEmpty()) {
            List<WorkerRegistration> savedRegistrations = workerRegistrationRepository.saveAll(registrationsToSave);

            for (WorkerRegistration savedRegistration : savedRegistrations) {
                log.info("Successfully registered worker: {} for step: {} in orchestration: {} with topic: {} and ID: {}",
                        serviceName, savedRegistration.getStepName(), orchName,
                        savedRegistration.getTopicName(), savedRegistration.getId());
            }

            return savedRegistrations;
        }

        return List.of();
    }
}

