package com.ecom.orchestrator.service;

import com.ecom.orchestrator.entity.OrchestrationStatusEnum;
import com.ecom.orchestrator.entity.OrchestrationTemplate;
import com.ecom.orchestrator.repository.OrchestrationTemplateRepository;
import com.ecom.orchestrator.repository.WorkerRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SelfHealingService {

    private final OrchestrationTemplateRepository orchestrationTemplateRepository;
    private final WorkerRegistrationRepository workerRegistrationRepository;

    //@Scheduled(fixedDelay = 60000) // Run every 60 seconds
    @Transactional
    public void performSelfHealing() {
        log.debug("Performing self-healing check for orchestrations");

        // Find orchestrations that are in FAILED or PENDING state
        List<OrchestrationTemplate> failedOrchestrations = orchestrationTemplateRepository
                .findByStatus(OrchestrationStatusEnum.FAILED);

        List<OrchestrationTemplate> pendingOrchestrations = orchestrationTemplateRepository
                .findByStatus(OrchestrationStatusEnum.PENDING);

        failedOrchestrations.addAll(pendingOrchestrations);

        for (OrchestrationTemplate template : failedOrchestrations) {
            checkAndHealOrchestration(template);
        }
    }

    private void checkAndHealOrchestration(OrchestrationTemplate template) {
        try {
            // Check if all steps now have workers registered
            boolean allStepsHaveWorkers = template.getSteps().stream()
                    .allMatch(step -> workerRegistrationRepository
                            .existsByOrchNameAndStepName(template.getOrchName(), step.getStepName()));

            if (allStepsHaveWorkers && template.getStatus() != OrchestrationStatusEnum.SUCCESS) {
                template.setStatus(OrchestrationStatusEnum.SUCCESS);
                template.setFailureReason(null);
                orchestrationTemplateRepository.save(template);

                log.info("Self-healing: Orchestration status updated to SUCCESS: {}", template.getOrchName());
            }
        } catch (Exception e) {
            log.error("Error during self-healing for orchestration: {}", template.getOrchName(), e);
        }
    }
}
