package com.ecom.orchestrator.service;

import com.ecom.orchestrator.dto.ExecutionMessage;
import com.ecom.orchestrator.entity.*;
import com.ecom.orchestrator.repository.*;
import com.ecom.orchestrator.messaging.interfaces.MessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrchestrationExecutorService {

    private final OrchestrationTemplateRepository orchestrationTemplateRepository;
    private final WorkerRegistrationRepository workerRegistrationRepository;
    private final OrchestrationRunRepository orchestrationRunRepository;
    private final OrchestrationStepRunRepository stepRunRepository;
    private final MessagePublisher messagePublisher;
    private final DoOperationHandler doOperationHandler;
    private final AuditService auditService;

    public OrchestrationExecutorService(
            OrchestrationTemplateRepository orchestrationTemplateRepository,
            WorkerRegistrationRepository workerRegistrationRepository,
            OrchestrationRunRepository orchestrationRunRepository,
            OrchestrationStepRunRepository stepRunRepository,
            MessagePublisher messagePublisher,
            DoOperationHandler doOperationHandler,
            AuditService auditService) {

        this.orchestrationTemplateRepository = orchestrationTemplateRepository;
        this.workerRegistrationRepository = workerRegistrationRepository;
        this.orchestrationRunRepository = orchestrationRunRepository;
        this.stepRunRepository = stepRunRepository;
        this.messagePublisher = messagePublisher;
        this.doOperationHandler = doOperationHandler;
        this.auditService = auditService;
    }

    @Transactional
    public String startOrchestration(String orchName, ExecutionMessage message) {
        log.info("Starting orchestration: {}", orchName);

        // Validate orchestration exists and is ready
        Optional<OrchestrationTemplate> templateOpt = orchestrationTemplateRepository
                .findByOrchNameWithSteps(orchName);

        // Generate flow ID
        String flowId = message.getHeaders().get("flowId") != null ?
                message.getHeaders().get("flowId").toString() : UUID.randomUUID().toString();
        log.info("Generated flowId: {} for orchestration: {}", flowId, orchName);
        // Create orchestration run
        OrchestrationRun orchestrationRun = OrchestrationRun.builder()
                .flowId(flowId)
                .orchName(orchName)
                .status(ExecutionStatusEnum.NOT_REGISTERED)
                .build();
        if (templateOpt.isEmpty()) {
            throw new IllegalArgumentException("Orchestration not found: " + orchName);
        }

        OrchestrationTemplate template = templateOpt.get();

        if (template.getStatus() != OrchestrationStatusEnum.SUCCESS) {
            orchestrationRunRepository.save(orchestrationRun);
            log.info("Orchestration not ready: " + orchName +
                    " Status: " + template.getStatus());
        }else {

            // Create orchestration run
            orchestrationRun = OrchestrationRun.builder()
                    .flowId(flowId)
                    .orchName(orchName)
                    .status(ExecutionStatusEnum.IN_PROGRESS)
                    .build();

            final OrchestrationRun savedOrchestrationRun = orchestrationRunRepository.save(orchestrationRun);

            // Record audit event for orchestration start
            String initiator = template.getInitiatorService();
            auditService.recordOrchestrationStart(flowId, orchName, initiator);

            // Create step runs with max retries from template
            List<OrchestrationStepRun> stepRuns = template.getSteps().stream()
                    .map(stepTemplate -> OrchestrationStepRun.builder()
                            .orchestrationRun(savedOrchestrationRun)
                            .stepName(stepTemplate.getStepName())
                            .seq(stepTemplate.getSeq())
                            .status(ExecutionStatusEnum.PENDING)
                            .maxRetries(stepTemplate.getMaxRetries())
                            .retryCount(0)
                            .build())
                    .collect(Collectors.toList());

            stepRunRepository.saveAll(stepRuns);
            savedOrchestrationRun.setStepRuns(stepRuns);

            // Execute using DoOperationHandler
            doOperationHandler.startDoOperations(flowId, template, message);
        }
        return flowId;
    }

    // Backward compatibility method - deprecated
    @Deprecated
    public void handleStepResponse(String flowId, String stepName, boolean success, String errorMessage, ExecutionMessage message) {
        log.warn("Using deprecated handleStepResponse method. Please use DoOperationHandler instead.");
        doOperationHandler.handleDoResponse(flowId, stepName, success, errorMessage, message);
    }
}
