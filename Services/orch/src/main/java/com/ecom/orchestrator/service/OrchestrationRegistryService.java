package com.ecom.orchestrator.service;

import com.ecom.orchestrator.dto.ExecutionMessage;
import com.ecom.orchestrator.dto.OrchestrationRegistrationDto;
import com.ecom.orchestrator.dto.RegistrationStatusEventDto;
import com.ecom.orchestrator.dto.StepDefinitionDto;
import com.ecom.orchestrator.entity.*;
import com.ecom.orchestrator.repository.*;
import com.ecom.orchestrator.messaging.interfaces.MessagePublisher;
import com.ecom.orchestrator.messaging.interfaces.TopicManager;
import com.ecom.orchestrator.serialization.Serializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrchestrationRegistryService {

    private final OrchestrationTemplateRepository orchestrationTemplateRepository;
    private final OrchestrationStepTemplateRepository stepTemplateRepository;
    private final WorkerRegistrationRepository workerRegistrationRepository;
    private final RegistrationAuditRepository registrationAuditRepository;

    private final MessagePublisher messagePublisher;
    private final TopicManager topicManager;




    public OrchestrationRegistryService(
            OrchestrationTemplateRepository orchestrationTemplateRepository,
            OrchestrationStepTemplateRepository stepTemplateRepository,
            WorkerRegistrationRepository workerRegistrationRepository,
            RegistrationAuditRepository registrationAuditRepository,
            MessagePublisher messagePublisher,
            TopicManager topicManager
             ) {

        this.orchestrationTemplateRepository = orchestrationTemplateRepository;
        this.stepTemplateRepository = stepTemplateRepository;
        this.workerRegistrationRepository = workerRegistrationRepository;
        this.registrationAuditRepository = registrationAuditRepository;
        this.messagePublisher = messagePublisher;
        this.topicManager = topicManager;
    }

    @Transactional
    public void registerOrchestration(OrchestrationRegistrationDto registrationDto, String serviceName) {
        log.info("Registering orchestration: {} as: {} by service: {}",
                registrationDto.getOrchestrationName(), registrationDto.getAs(), serviceName);

        if ("initiator".equalsIgnoreCase(registrationDto.getAs())) {
            registerInitiator(registrationDto, serviceName);
        } else if ("worker".equalsIgnoreCase(registrationDto.getAs())) {
            registerWorker(registrationDto, serviceName);
        } else {
            throw new IllegalArgumentException("Invalid role: " + registrationDto.getAs());
        }
    }

    private void registerInitiator(OrchestrationRegistrationDto registrationDto, String serviceName) {
        List<String> failedSteps = new ArrayList<>();
        RegistrationStatusEnum status = RegistrationStatusEnum.SUCCESS;

        try {
            // Use upsert approach to preserve existing ID and avoid race conditions
            Optional<OrchestrationTemplate> existingTemplate = orchestrationTemplateRepository
                    .findByOrchName(registrationDto.getOrchestrationName());

            OrchestrationTemplate template;
            if (existingTemplate.isPresent()) {
                log.info("Orchestration already exists: {}, updating existing template",
                        registrationDto.getOrchestrationName());

                template = existingTemplate.get();
                // Update existing template fields
                template.setType(OrchestrationTypeEnum.valueOf(registrationDto.getType().toUpperCase()));
                template.setInitiatorService(serviceName);
                template.setStatus(OrchestrationStatusEnum.PENDING);
                template.setFailureReason(null); // Clear any previous failure reason

                // Delete existing step templates to recreate them
                List<OrchestrationStepTemplate> existingSteps = stepTemplateRepository.findByTemplateOrchNameOrderBySeq(registrationDto.getOrchestrationName());
                if (!existingSteps.isEmpty()) {
                    stepTemplateRepository.deleteAll(existingSteps);
                    log.info("Deleted {} existing step templates for orchestration: {}", existingSteps.size(), registrationDto.getOrchestrationName());
                }
            } else {
                template = OrchestrationTemplate.builder()
                        .orchName(registrationDto.getOrchestrationName())
                        .type(OrchestrationTypeEnum.valueOf(registrationDto.getType().toUpperCase()))
                        .initiatorService(serviceName)
                        .status(OrchestrationStatusEnum.PENDING)
                        .build();
                log.info("Creating new orchestration template: {}", registrationDto.getOrchestrationName());
                List<String> requestStepNames = registrationDto.getSteps().stream()
                        .map(StepDefinitionDto::getName)
                        .collect(Collectors.toList());
                List<WorkerRegistration> existingRegistrations = workerRegistrationRepository
                        .findByOrchNameAndWorkerServiceAndStepNameIn(
                                registrationDto.getOrchestrationName(), serviceName, requestStepNames);

                if (!existingRegistrations.isEmpty()) {
                    template.setStatus(OrchestrationStatusEnum.SUCCESS);
                }
            }

            if (status == RegistrationStatusEnum.SUCCESS) {
                template = orchestrationTemplateRepository.save(template);
                log.info("Saved orchestration template: {} with ID: {}",
                        registrationDto.getOrchestrationName(), template.getId());
                    // Create step templates
                    Set<String> processedSteps = new HashSet<>(); // Track steps in current request
                    Set<Integer> processedSeq = new HashSet<>(); // Track sequence numbers in current request
                    for (StepDefinitionDto stepDto : registrationDto.getSteps()) {
                        // Check for duplicate step names within the same registration request
                        if (processedSteps.contains(stepDto.getName())) {
                            log.warn("Duplicate step name found in registration request: {} for orchestration: {}",
                                    stepDto.getName(), registrationDto.getOrchestrationName());
                            failedSteps.add("Duplicate step name in request: " + stepDto.getName());
                            status = RegistrationStatusEnum.FAILED;
                            continue;
                        }

                        // Check for duplicate sequence numbers within the same registration request
                        if (processedSeq.contains(stepDto.getSeq())) {
                            log.warn("Duplicate sequence number found in registration request: {} for step: {} in orchestration: {}",
                                    stepDto.getSeq(), stepDto.getName(), registrationDto.getOrchestrationName());
                            failedSteps.add("Duplicate sequence number " + stepDto.getSeq() + " for step: " + stepDto.getName());
                            status = RegistrationStatusEnum.FAILED;
                            continue;
                        }

                        // Check if step template already exists in database (should not happen after delete above, but safety check)
                        boolean stepExists = stepTemplateRepository.existsByTemplateOrchNameAndStepName(
                                registrationDto.getOrchestrationName(), stepDto.getName());

                        if (stepExists) {
                            log.warn("Step template already exists: {} for orchestration: {}",
                                    stepDto.getName(), registrationDto.getOrchestrationName());
                            failedSteps.add("Step template already exists: " + stepDto.getName());
                            status = RegistrationStatusEnum.FAILED;
                            continue;
                        }

                        processedSteps.add(stepDto.getName()); // Mark step name as processed
                        processedSeq.add(stepDto.getSeq()); // Mark sequence number as processed
                        String topicName = generateTopicName(registrationDto.getOrchestrationName(), stepDto.getName());

                        OrchestrationStepTemplate stepTemplate = OrchestrationStepTemplate.builder()
                                .template(template)
                                .seq(stepDto.getSeq())
                                .stepName(stepDto.getName())
                                .objectType(stepDto.getObjectType())
                                .topicName(topicName)
                                .build();

                        stepTemplateRepository.save(stepTemplate);

                        log.info("Created step template: {} with topic: {} and ID: {} for orchestration: {}",
                                stepDto.getName(), topicName, stepTemplate.getId(), registrationDto.getOrchestrationName());
                        // Create topic
                        topicManager.createTopic(topicName);
                        status = RegistrationStatusEnum.PENDING;
                    }

                    // Check if all steps have workers registered
                    updateOrchestrationStatus(registrationDto.getOrchestrationName());
                }

        } catch (Exception e) {
            log.error("Error registering initiator", e);
            failedSteps.add("Internal error: " + e.getMessage());
            status = RegistrationStatusEnum.FAILED;
        }

        // Audit registration
        auditRegistration(registrationDto.getOrchestrationName(), RegistrationRoleEnum.INITIATOR,
                serviceName, status, registrationDto.getSteps(), failedSteps);

        // Publish registration status
        publishRegistrationStatus(registrationDto.getOrchestrationName(), "initiator", serviceName, status, failedSteps);
    }

    private void registerWorker(OrchestrationRegistrationDto registrationDto, String serviceName) {
        log.info("Starting worker registration for orchestration: {} by service: {} with {} steps",
                registrationDto.getOrchestrationName(), serviceName, registrationDto.getSteps().size());

        List<String> failedSteps = new ArrayList<>();
        List<StepDefinitionDto> successfulSteps = new ArrayList<>();
        RegistrationStatusEnum status = RegistrationStatusEnum.SUCCESS;

        try {
            log.info("Validating orchestration existence: {}", registrationDto.getOrchestrationName());

            // Check if orchestration exists
            Optional<OrchestrationTemplate> templateOpt = orchestrationTemplateRepository
                    .findByOrchNameWithSteps(registrationDto.getOrchestrationName());
            // Track processed steps in current request to prevent duplicates
            Set<String> processedSteps = new HashSet<>();

            if (templateOpt.isEmpty()) {
                log.warn("Orchestration template not found: {}", registrationDto.getOrchestrationName());
                failedSteps.add("Orchestration not found: " + registrationDto.getOrchestrationName());
                status = RegistrationStatusEnum.FAILED;
            } else {
                OrchestrationTemplate template = templateOpt.get();
                log.info("Found orchestration template: {} with {} defined steps",
                        registrationDto.getOrchestrationName(), template.getSteps().size());
                // Check if any step from the request already has worker registrations for this service
                List<String> requestStepNames = registrationDto.getSteps().stream()
                        .map(StepDefinitionDto::getName)
                        .collect(Collectors.toList());

                List<WorkerRegistration> existingRegistrations = workerRegistrationRepository
                        .findByOrchNameAndWorkerServiceAndStepNameIn(
                                registrationDto.getOrchestrationName(), serviceName, requestStepNames);

                if (!existingRegistrations.isEmpty()) {
                    log.info("Found {} existing worker registrations for service: {} in orchestration: {}, deleting them",
                            existingRegistrations.size(), serviceName, registrationDto.getOrchestrationName());

                    // Log each registration being deleted
                    for (WorkerRegistration reg : existingRegistrations) {
                        log.info("Deleting existing worker registration ID: {} for step: {} service: {}",
                                reg.getId(), reg.getStepName(), reg.getWorkerService());
                    }

                    // Delete existing registrations for this service and these steps
                    workerRegistrationRepository.deleteAll(existingRegistrations);
                    log.info("Deleted {} existing worker registrations for service: {} in orchestration: {}",
                            existingRegistrations.size(), serviceName, registrationDto.getOrchestrationName());
                }

                processWorkerStepRegistrations(registrationDto, serviceName, template, failedSteps, successfulSteps, processedSteps);

                log.info("Worker registration summary - successful steps: {}, failed steps: {} for orchestration: {}",
                        successfulSteps.size(), failedSteps.size(), registrationDto.getOrchestrationName());

                // Update orchestration status (self-healing)
                log.info("Updating orchestration status after worker registration for: {}",
                        registrationDto.getOrchestrationName());
                updateOrchestrationStatus(registrationDto.getOrchestrationName());
            }
        } catch (Exception e) {
            log.error("Error registering worker for orchestration: {} by service: {}",
                    registrationDto.getOrchestrationName(), serviceName, e);
            failedSteps.add("Internal error: " + e.getMessage());
            status = RegistrationStatusEnum.FAILED;
        }

        log.info("Completing worker registration for orchestration: {} with final status: {}",
                registrationDto.getOrchestrationName(), status);

        // Audit registration
        auditRegistration(registrationDto.getOrchestrationName(), RegistrationRoleEnum.WORKER,
                serviceName, status, successfulSteps, failedSteps);

        // Publish registration status
        publishRegistrationStatus(registrationDto.getOrchestrationName(), "worker", serviceName, status, failedSteps);
    }

    private void processWorkerStepRegistrations(
            OrchestrationRegistrationDto registrationDto,
            String serviceName,
            OrchestrationTemplate template,
            List<String> failedSteps,
            List<StepDefinitionDto> successfulSteps,
            Set<String> processedSteps) {

        for (StepDefinitionDto stepDto : registrationDto.getSteps()) {
            log.info("Processing step registration: {} for orchestration: {}",
                    stepDto.getName(), registrationDto.getOrchestrationName());

            // Check for duplicate step names within the same worker registration request
            if (processedSteps.contains(stepDto.getName())) {
                log.warn("Duplicate step name found in worker registration request: {} for orchestration: {}",
                        stepDto.getName(), registrationDto.getOrchestrationName());
                failedSteps.add("Duplicate step name in request: " + stepDto.getName());
                continue;
            }

            // Check if step exists in orchestration
            Optional<OrchestrationStepTemplate> stepTemplateOpt = template.getSteps().stream()
                    .filter(st -> st.getStepName().equals(stepDto.getName()))
                    .findFirst();

            if (stepTemplateOpt.isEmpty()) {
                log.warn("Step not found in orchestration template: {} for orchestration: {}",
                        stepDto.getName(), registrationDto.getOrchestrationName());
                failedSteps.add("Step not found in orchestration: " + stepDto.getName());
                continue;
            }

            OrchestrationStepTemplate stepTemplate = stepTemplateOpt.get();
            log.info("Found step template: {} with topic: {}",
                    stepTemplate.getStepName(), stepTemplate.getTopicName());

            // Check if worker is already registered for this step (safety check after delete above)
            boolean workerExists = workerRegistrationRepository.existsByOrchNameAndStepNameAndWorkerService(
                    registrationDto.getOrchestrationName(), stepDto.getName(), serviceName);

            if (workerExists) {
                log.warn("Worker already registered for step: {} in orchestration: {} by service: {}",
                        stepDto.getName(), registrationDto.getOrchestrationName(), serviceName);
                failedSteps.add("Worker already registered for step: " + stepDto.getName());
                continue;
            }

            processedSteps.add(stepDto.getName()); // Mark step as processed

            // Register worker for step
            WorkerRegistration registration = WorkerRegistration.builder()
                    .orchName(registrationDto.getOrchestrationName())
                    .stepName(stepDto.getName())
                    .workerService(serviceName)
                    .topicName(stepTemplate.getTopicName())
                    .build();

            workerRegistrationRepository.save(registration);
            successfulSteps.add(stepDto);
            log.info("Successfully registered worker: {} for step: {} in orchestration: {} with topic: {} and ID: {}",
                    serviceName, stepDto.getName(), registrationDto.getOrchestrationName(),
                    stepTemplate.getTopicName(), registration.getId());
        }
    }


    private void updateOrchestrationStatus(String orchName) {
        Optional<OrchestrationTemplate> templateOpt = orchestrationTemplateRepository
                .findByOrchNameWithSteps(orchName);

        if (templateOpt.isEmpty()) {
            log.warn("Orchestration template not found: {}", orchName);
            return;
        }

        OrchestrationTemplate template = templateOpt.get();

        // Explicitly initialize the steps collection to handle lazy loading issues
        List<OrchestrationStepTemplate> steps = initializeSteps(template, orchName);
        if (steps.isEmpty()) {
            log.warn("No steps found for orchestration: {}", orchName);
            return;
        }

        // Check if all steps have workers
        boolean allStepsHaveWorkers = steps.stream()
                .allMatch(step -> workerRegistrationRepository
                        .existsByOrchNameAndStepName(orchName, step.getStepName()));

        if (allStepsHaveWorkers && template.getStatus() != OrchestrationStatusEnum.SUCCESS) {
            template.setStatus(OrchestrationStatusEnum.SUCCESS);
            template.setFailureReason(null);
            orchestrationTemplateRepository.save(template);
            log.info("Orchestration status updated to SUCCESS: {}", orchName);
        } else if (!allStepsHaveWorkers) {
            log.debug("Not all steps have workers registered for orchestration: {}", orchName);
        }
    }

    /**
     * Initialize and return the steps collection, handling potential lazy loading issues
     */
    private List<OrchestrationStepTemplate> initializeSteps(OrchestrationTemplate template, String orchName) {
        try {
            // First try to get steps from the template
            List<OrchestrationStepTemplate> steps = template.getSteps();

            // If steps is null or empty, try to fetch directly from repository as fallback
            if (steps == null || steps.isEmpty()) {
                log.debug("Steps collection is null/empty, fetching directly from repository for: {}", orchName);
                steps = stepTemplateRepository.findByTemplateOrchNameOrderBySeq(orchName);
            }

            // Force initialization of the collection if it's a Hibernate proxy
            if (steps != null) {
                steps.size(); // This will trigger lazy loading if needed
            }

            return steps != null ? steps : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error initializing steps for orchestration: {}", orchName, e);
            // Fallback: fetch steps directly from repository
            return stepTemplateRepository.findByTemplateOrchNameOrderBySeq(orchName);
        }
    }

    private void auditRegistration(String orchName, RegistrationRoleEnum role, String serviceName,
                                   RegistrationStatusEnum status, List<StepDefinitionDto> steps,
                                   List<String> failedSteps) {
        try {
            // Convert steps list to Map for JSONB storage
            Map<String, Object> registeredStepsMap = new HashMap<>();
            registeredStepsMap.put("steps", steps);

            // Convert failed steps list to Map for JSONB storage
            Map<String, Object> failedStepsMap = null;
            if (!failedSteps.isEmpty()) {
                failedStepsMap = new HashMap<>();
                failedStepsMap.put("errors", failedSteps);
            }

            RegistrationAudit audit = RegistrationAudit.builder()
                    .orchName(orchName)
                    .asRole(role)
                    .serviceName(serviceName)
                    .status(status)
                    .registeredSteps(registeredStepsMap)
                    .failedSteps(failedStepsMap)
                    .build();

            registrationAuditRepository.save(audit);
        } catch (Exception e) {
            log.error("Error auditing registration", e);
        }
    }

    private void publishRegistrationStatus(String orchName, String asRole, String serviceName,
                                           RegistrationStatusEnum status, List<String> failedSteps) {
        try {
            Map<String, String> failureReason = failedSteps.isEmpty() ? null :
                    failedSteps.stream().collect(Collectors.toMap(
                            failure -> "error_" + failedSteps.indexOf(failure),
                            failure -> failure));

            RegistrationStatusEventDto event = RegistrationStatusEventDto.builder()
                    .eventType("REGISTRATION_STATUS")
                    .orchName(orchName)
                    .as(asRole)
                    .serviceName(serviceName)
                    .status(status.name())
                    .failureReason(failureReason)
                    .timestamp(LocalDateTime.now())
                    .build();


            // Create Message object with payload and headers
            HashMap<String, Object> headers = new HashMap<>();
            headers.put("eventType", "REGISTRATION_STATUS");
            headers.put("orchName", orchName);
            headers.put("asRole", asRole);
            headers.put("serviceName", serviceName);
            headers.put("status", status.name());

            ExecutionMessage message = ExecutionMessage.builder().payload(event).headers(headers).build();


            messagePublisher.send("orchestrator.registration.status", message);

            log.info("Published registration status event for: {} as: {} status: {}",
                    orchName, asRole, status);
        } catch (Exception e) {
            log.error("Error publishing registration status", e);
        }
    }

    private String generateTopicName(String orchName, String stepName) {
        return String.format("orchestrator.%s.%s", orchName, stepName);
    }
}
