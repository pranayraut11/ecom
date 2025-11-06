package com.ecom.orchestrator.service;

import com.ecom.orchestrator.dto.OrchestrationDetailsResponseDto;
import com.ecom.orchestrator.dto.StepDetailsDto;
import com.ecom.orchestrator.entity.OrchestrationStatusEnum;
import com.ecom.orchestrator.entity.OrchestrationStepTemplate;
import com.ecom.orchestrator.entity.OrchestrationTemplate;
import com.ecom.orchestrator.entity.WorkerRegistration;
import com.ecom.orchestrator.repository.OrchestrationTemplateRepository;
import com.ecom.orchestrator.repository.WorkerRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrchestrationDetailsService {

    private final OrchestrationTemplateRepository orchestrationTemplateRepository;
    private final WorkerRegistrationRepository workerRegistrationRepository;

    public OrchestrationDetailsResponseDto getOrchestrationDetails(String orchName) {
        log.info("Fetching orchestration details for: {}", orchName);

        // Find the orchestration template
        Optional<OrchestrationTemplate> templateOpt = orchestrationTemplateRepository.findByOrchName(orchName);

        if (templateOpt.isEmpty()) {
            log.warn("Orchestration not found: {}", orchName);
            return null; // This will be handled by the controller to return 404
        }

        OrchestrationTemplate template = templateOpt.get();

        // Get all worker registrations for this orchestration
        List<WorkerRegistration> workerRegistrations = workerRegistrationRepository.findByOrchName(orchName);
        Map<String, WorkerRegistration> workerRegistrationMap = workerRegistrations.stream()
                .collect(Collectors.toMap(WorkerRegistration::getStepName, wr -> wr));

        // Build step details
        List<StepDetailsDto> stepDetails = template.getSteps().stream()
                .sorted(Comparator.comparingInt(OrchestrationStepTemplate::getSeq))
                .map(stepTemplate -> buildStepDetails(stepTemplate, workerRegistrationMap, template.getInitiatorService()))
                .collect(Collectors.toList());

        log.info("Retrieved details for orchestration: {} with {} steps", orchName, stepDetails.size());

        return OrchestrationDetailsResponseDto.builder()
                .orchName(template.getOrchName())
                .type(template.getType().name())
                .status(mapStatusToApi(template.getStatus()))
                .initiator(template.getInitiatorService())
                .createdAt(template.getCreatedAt())
                .steps(stepDetails)
                .build();
    }

    private StepDetailsDto buildStepDetails(OrchestrationStepTemplate stepTemplate,
                                          Map<String, WorkerRegistration> workerRegistrationMap,
                                          String initiatorService) {

        WorkerRegistration workerRegistration = workerRegistrationMap.get(stepTemplate.getStepName());

        String registeredBy;
        String status;
        String failureReason = null;

        if (workerRegistration != null) {
            // Step is registered by a worker
            registeredBy = workerRegistration.getWorkerService();
            status = "SUCCESS";
        } else {
            // Step is defined by initiator but not registered by any worker yet
            registeredBy = initiatorService;
            status = "PENDING";
        }

        return StepDetailsDto.builder()
                .seq(stepTemplate.getSeq())
                .name(stepTemplate.getStepName())
                .objectType(stepTemplate.getObjectType())
                .registeredBy(registeredBy)
                .status(status)
                .failureReason(failureReason)
                .build();
    }

    private String mapStatusToApi(OrchestrationStatusEnum status) {
        return switch (status) {
            case SUCCESS -> "REGISTERED";
            case PENDING -> "PARTIALLY_REGISTERED";
            case FAILED -> "FAILED";
        };
    }
}
