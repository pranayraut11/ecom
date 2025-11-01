package com.ecom.orchestrator.service.strategy;

import com.ecom.orchestrator.constant.RegistrationConstants;
import com.ecom.orchestrator.dto.OrchestrationRegistrationDto;
import com.ecom.orchestrator.dto.RegistrationResult;
import com.ecom.orchestrator.dto.StepDefinitionDto;
import com.ecom.orchestrator.entity.*;
import com.ecom.orchestrator.mapper.OrchestrationMapper;
import com.ecom.orchestrator.repository.OrchestrationTemplateRepository;
import com.ecom.orchestrator.repository.WorkerRegistrationRepository;
import com.ecom.orchestrator.service.WorkerRegistrationHelper;
import com.ecom.orchestrator.validator.WorkerRegistrationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Strategy for handling worker registration
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WorkerRegistrationStrategy implements RegistrationStrategy {

    private final OrchestrationTemplateRepository orchestrationTemplateRepository;
    private final WorkerRegistrationRepository workerRegistrationRepository;
    private final OrchestrationMapper orchestrationMapper;
    private final WorkerRegistrationValidator workerValidator;
    private final WorkerRegistrationHelper workerRegistrationHelper;

    @Override
    public RegistrationResult register(OrchestrationRegistrationDto registrationDto, String serviceName) {
        log.info("Executing worker registration strategy for: {} by service: {} with {} steps",
                registrationDto.getOrchestrationName(), serviceName, registrationDto.getSteps().size());

        List<String> failedSteps = new ArrayList<>();
        List<StepDefinitionDto> successfulSteps = new ArrayList<>();
        RegistrationStatusEnum status = RegistrationStatusEnum.SUCCESS;

        try {
            Optional<OrchestrationTemplate> templateOpt = orchestrationTemplateRepository
                    .findByOrchNameWithSteps(registrationDto.getOrchestrationName());

            // Delete existing registrations first
            List<String> requestStepNames = getRequestStepNames(registrationDto);
            deleteExistingWorkerRegistrations(registrationDto.getOrchestrationName(), serviceName, requestStepNames);

            if (templateOpt.isEmpty()) {
                log.warn("Orchestration template not found: {}", registrationDto.getOrchestrationName());
                failedSteps.add("Orchestration not found: " + registrationDto.getOrchestrationName());
                status = RegistrationStatusEnum.FAILED;

                // Register workers anyway (for template-not-found case)
                workerRegistrationHelper.registerWorkersInBatch(
                        registrationDto.getOrchestrationName(),
                        serviceName,
                        registrationDto.getSteps(),
                        stepName -> stepName
                );
            } else {
                OrchestrationTemplate template = templateOpt.get();
                log.info("Found orchestration template: {} with {} defined steps",
                        registrationDto.getOrchestrationName(), template.getSteps().size());

                processWorkerStepRegistrations(registrationDto, serviceName, template, failedSteps, successfulSteps);
            }
        } catch (Exception e) {
            log.error("Error in worker registration strategy for orchestration: {} by service: {}",
                    registrationDto.getOrchestrationName(), serviceName, e);
            failedSteps.add("Internal error: " + e.getMessage());
            status = RegistrationStatusEnum.FAILED;
        }

        return RegistrationResult.builder()
                .status(status)
                .failedSteps(failedSteps)
                .successfulSteps(successfulSteps)
                .build();
    }

    @Override
    public String getRole() {
        return RegistrationConstants.ROLE_WORKER;
    }

    private void processWorkerStepRegistrations(
            OrchestrationRegistrationDto registrationDto,
            String serviceName,
            OrchestrationTemplate template,
            List<String> failedSteps,
            List<StepDefinitionDto> successfulSteps) {

        Map<String, String> validationErrors = workerValidator.validateSteps(
                registrationDto.getSteps(), template, serviceName, registrationDto.getOrchestrationName());

        failedSteps.addAll(validationErrors.values());

        List<WorkerRegistration> registrationsToSave = new ArrayList<>();

        for (StepDefinitionDto stepDto : registrationDto.getSteps()) {
            if (validationErrors.containsKey(stepDto.getName())) {
                continue;
            }

            Optional<OrchestrationStepTemplate> stepTemplateOpt =
                    workerValidator.findStepTemplate(template, stepDto.getName());

            if (stepTemplateOpt.isEmpty()) {
                continue;
            }

            OrchestrationStepTemplate stepTemplate = stepTemplateOpt.get();

            WorkerRegistration registration = orchestrationMapper.toWorkerRegistration(
                    registrationDto.getOrchestrationName(),
                    stepDto.getName(),
                    serviceName,
                    stepTemplate.getTopicName());

            registrationsToSave.add(registration);
            successfulSteps.add(stepDto);
        }

        if (!registrationsToSave.isEmpty()) {
            List<WorkerRegistration> savedRegistrations = workerRegistrationRepository.saveAll(registrationsToSave);

            for (WorkerRegistration savedRegistration : savedRegistrations) {
                log.info("Successfully registered worker: {} for step: {} in orchestration: {} with topic: {} and ID: {}",
                        serviceName, savedRegistration.getStepName(), registrationDto.getOrchestrationName(),
                        savedRegistration.getTopicName(), savedRegistration.getId());
            }
        }
    }

    private List<String> getRequestStepNames(OrchestrationRegistrationDto registrationDto) {
        return registrationDto.getSteps().stream()
                .map(StepDefinitionDto::getName)
                .collect(Collectors.toList());
    }

    private void deleteExistingWorkerRegistrations(String orchName, String serviceName, List<String> stepNames) {
        List<WorkerRegistration> existingRegistrations = workerRegistrationRepository
                .findByOrchNameAndWorkerServiceAndStepNameIn(orchName, serviceName, stepNames);

        if (!existingRegistrations.isEmpty()) {
            log.info("Found {} existing worker registrations for service: {} in orchestration: {}, deleting them",
                    existingRegistrations.size(), serviceName, orchName);

            workerRegistrationRepository.deleteAll(existingRegistrations);
            log.info("Deleted {} existing worker registrations for service: {} in orchestration: {}",
                    existingRegistrations.size(), serviceName, orchName);
        }
    }
}

