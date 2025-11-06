package com.ecom.orchestrator.service.strategy;

import com.ecom.orchestrator.constant.RegistrationConstants;
import com.ecom.orchestrator.dto.OrchestrationRegistrationDto;
import com.ecom.orchestrator.dto.RegistrationResult;
import com.ecom.orchestrator.entity.*;
import com.ecom.orchestrator.mapper.OrchestrationMapper;
import com.ecom.orchestrator.messaging.interfaces.TopicManager;
import com.ecom.orchestrator.repository.OrchestrationStepTemplateRepository;
import com.ecom.orchestrator.repository.OrchestrationTemplateRepository;
import com.ecom.orchestrator.repository.WorkerRegistrationRepository;
import com.ecom.orchestrator.validator.InitiatorRegistrationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Strategy for handling initiator registration
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InitiatorRegistrationStrategy implements RegistrationStrategy {

    private final OrchestrationTemplateRepository orchestrationTemplateRepository;
    private final OrchestrationStepTemplateRepository stepTemplateRepository;
    private final WorkerRegistrationRepository workerRegistrationRepository;
    private final OrchestrationMapper orchestrationMapper;
    private final InitiatorRegistrationValidator initiatorValidator;
    private final TopicManager topicManager;

    @Override
    public RegistrationResult register(OrchestrationRegistrationDto registrationDto, String serviceName) {
        log.info("Executing initiator registration strategy for: {}", registrationDto.getOrchestrationName());

        List<String> failedSteps = new ArrayList<>();
        RegistrationStatusEnum status = RegistrationStatusEnum.SUCCESS;

        try {
            OrchestrationTemplate template = findOrCreateTemplate(registrationDto, serviceName);

            if (status == RegistrationStatusEnum.SUCCESS) {
                template = orchestrationTemplateRepository.save(template);
                log.info("Saved orchestration template: {} with ID: {}",
                        registrationDto.getOrchestrationName(), template.getId());

                // Create step templates
                status = processStepTemplates(registrationDto, template, failedSteps);
            }

        } catch (Exception e) {
            log.error("Error in initiator registration strategy", e);
            failedSteps.add("Internal error: " + e.getMessage());
            status = RegistrationStatusEnum.FAILED;
        }

        return RegistrationResult.builder()
                .status(status)
                .failedSteps(failedSteps)
                .successfulSteps(registrationDto.getSteps())
                .build();
    }

    @Override
    public String getRole() {
        return RegistrationConstants.ROLE_INITIATOR;
    }

    private OrchestrationTemplate findOrCreateTemplate(OrchestrationRegistrationDto registrationDto, String serviceName) {
        Optional<OrchestrationTemplate> existingTemplate = orchestrationTemplateRepository
                .findByOrchName(registrationDto.getOrchestrationName());

        if (existingTemplate.isPresent()) {
            log.info("Orchestration already exists: {}, updating existing template",
                    registrationDto.getOrchestrationName());

            OrchestrationTemplate template = existingTemplate.get();
            orchestrationMapper.updateOrchestrationTemplate(template, registrationDto, serviceName);

            // Delete existing step templates to recreate them
            List<OrchestrationStepTemplate> existingSteps = stepTemplateRepository
                    .findByTemplateOrchNameOrderBySeq(registrationDto.getOrchestrationName());
            if (!existingSteps.isEmpty()) {
                stepTemplateRepository.deleteAll(existingSteps);
                log.info("Deleted {} existing step templates for orchestration: {}",
                        existingSteps.size(), registrationDto.getOrchestrationName());
            }
            return template;
        } else {
            log.info("Creating new orchestration template: {}", registrationDto.getOrchestrationName());
            return orchestrationMapper.toOrchestrationTemplate(registrationDto, serviceName);
        }
    }

    private RegistrationStatusEnum processStepTemplates(
            OrchestrationRegistrationDto registrationDto,
            OrchestrationTemplate template,
            List<String> failedSteps) {

        // Validate all steps
        Map<String, String> validationErrors = initiatorValidator.validateSteps(
                registrationDto.getSteps(), registrationDto.getOrchestrationName());

        failedSteps.addAll(validationErrors.values());

        if (!initiatorValidator.isValid(validationErrors)) {
            return RegistrationStatusEnum.FAILED;
        }

        // Create step templates
        List<OrchestrationStepTemplate> stepTemplatesToSave = new ArrayList<>();

        for (var stepDto : registrationDto.getSteps()) {
            if (validationErrors.containsKey(stepDto.getName())) {
                continue;
            }

            OrchestrationStepTemplate stepTemplate = orchestrationMapper.toStepTemplate(
                    stepDto, template, registrationDto.getOrchestrationName());
            stepTemplatesToSave.add(stepTemplate);
        }

        // Batch save
        if (!stepTemplatesToSave.isEmpty()) {
            List<OrchestrationStepTemplate> savedTemplates = stepTemplateRepository.saveAll(stepTemplatesToSave);

            for (OrchestrationStepTemplate savedTemplate : savedTemplates) {
                log.info("Created step template: {} with DO topic: {}, UNDO topic: {} and ID: {} for orchestration: {}",
                        savedTemplate.getStepName(),
                        savedTemplate.getDoTopic(),
                        savedTemplate.getUndoTopic(),
                        savedTemplate.getId(),
                        registrationDto.getOrchestrationName());

                // Create DO topic
                topicManager.createTopic(savedTemplate.getDoTopic());
                log.info("Created DO topic: {}", savedTemplate.getDoTopic());

                // Create UNDO topic
                topicManager.createTopic(savedTemplate.getUndoTopic());
                log.info("Created UNDO topic: {}", savedTemplate.getUndoTopic());

                // Create legacy topic for backward compatibility
                topicManager.createTopic(savedTemplate.getTopicName());
                log.info("Created legacy topic: {} for backward compatibility", savedTemplate.getTopicName());
            }

            return RegistrationStatusEnum.PENDING;
        }

        return RegistrationStatusEnum.SUCCESS;
    }
}

