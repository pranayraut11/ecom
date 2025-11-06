package com.ecom.orchestrator.service;

import com.ecom.orchestrator.dto.AuditEventDto;
import com.ecom.orchestrator.dto.AuditTimelineResponseDto;
import com.ecom.orchestrator.entity.*;
import com.ecom.orchestrator.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for recording and retrieving audit events.
 * Records events asynchronously to avoid blocking the main orchestration flow.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    /**
     * Record an audit event asynchronously
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordEvent(AuditEvent event) {
        try {
            auditEventRepository.save(event);
            log.debug("Audit event recorded: executionId={}, eventType={}, stepName={}",
                event.getExecutionId(), event.getEventType(), event.getStepName());
        } catch (Exception e) {
            log.error("Failed to record audit event: executionId={}, eventType={}",
                event.getExecutionId(), event.getEventType(), e);
        }
    }

    /**
     * Get complete timeline for an execution
     */
    public AuditTimelineResponseDto getTimeline(String executionId) {
        log.info("Fetching audit timeline for executionId: {}", executionId);

        List<AuditEvent> events = auditEventRepository.findByExecutionIdOrderByTimestampAsc(executionId);

        if (events.isEmpty()) {
            log.warn("No audit events found for executionId: {}", executionId);
            return null;
        }

        List<AuditEventDto> eventDtos = events.stream()
            .map(this::toDto)
            .collect(Collectors.toList());

        String orchName = events.get(0).getOrchName();
        int failedEvents = (int) events.stream()
            .filter(e -> e.getEventType().name().contains("FAILED"))
            .count();
        int retryEvents = (int) events.stream()
            .filter(e -> e.getEventType() == AuditEventTypeEnum.STEP_RETRY_TRIGGERED)
            .count();

        return AuditTimelineResponseDto.builder()
            .executionId(executionId)
            .orchName(orchName)
            .totalEvents(events.size())
            .failedEvents(failedEvents)
            .retryEvents(retryEvents)
            .events(eventDtos)
            .build();
    }

    /**
     * Get timeline with filters
     */
    public AuditTimelineResponseDto getTimelineWithFilters(
            String executionId,
            AuditEventTypeEnum eventType,
            String status,
            LocalDateTime from,
            LocalDateTime to) {

        log.info("Fetching filtered audit timeline for executionId: {}", executionId);

        List<AuditEvent> events = auditEventRepository.findByExecutionIdWithFilters(
            executionId, eventType, status, from, to);

        if (events.isEmpty()) {
            return AuditTimelineResponseDto.builder()
                .executionId(executionId)
                .totalEvents(0)
                .events(List.of())
                .build();
        }

        List<AuditEventDto> eventDtos = events.stream()
            .map(this::toDto)
            .collect(Collectors.toList());

        return AuditTimelineResponseDto.builder()
            .executionId(executionId)
            .orchName(events.get(0).getOrchName())
            .totalEvents(events.size())
            .events(eventDtos)
            .build();
    }

    // ===== Helper Methods for Recording Specific Events =====

    /**
     * Record orchestration start event
     */
    public void recordOrchestrationStart(String executionId, String orchName, String initiator) {
        Map<String, Object> details = new HashMap<>();
        details.put("initiator", initiator);
        details.put("message", "Orchestration execution started");

        AuditEvent event = AuditEvent.builder()
            .executionId(executionId)
            .orchName(orchName)
            .entityType(AuditEntityTypeEnum.ORCHESTRATION)
            .eventType(AuditEventTypeEnum.ORCHESTRATION_STARTED)
            .timestamp(LocalDateTime.now())
            .createdBy(initiator)
            .serviceName(initiator)
            .details(details)
            .build();

        recordEvent(event);
    }

    /**
     * Record orchestration completion event
     */
    public void recordOrchestrationComplete(String executionId, String orchName, String status, Long durationMs) {
        Map<String, Object> details = new HashMap<>();
        details.put("durationMs", durationMs);
        details.put("message", "Orchestration execution completed");

        AuditEvent event = AuditEvent.builder()
            .executionId(executionId)
            .orchName(orchName)
            .entityType(AuditEntityTypeEnum.ORCHESTRATION)
            .eventType(AuditEventTypeEnum.ORCHESTRATION_COMPLETED)
            .status(status)
            .timestamp(LocalDateTime.now())
            .durationMs(durationMs)
            .details(details)
            .build();

        recordEvent(event);
    }

    /**
     * Record orchestration failure event
     */
    public void recordOrchestrationFailure(String executionId, String orchName, String reason) {
        Map<String, Object> details = new HashMap<>();
        details.put("message", "Orchestration execution failed");

        AuditEvent event = AuditEvent.builder()
            .executionId(executionId)
            .orchName(orchName)
            .entityType(AuditEntityTypeEnum.ORCHESTRATION)
            .eventType(AuditEventTypeEnum.ORCHESTRATION_FAILED)
            .status("FAILED")
            .timestamp(LocalDateTime.now())
            .reason(reason)
            .details(details)
            .build();

        recordEvent(event);
    }

    /**
     * Record step start event
     */
    public void recordStepStart(String executionId, String orchName, String stepName,
                                String workerService, String operationType) {
        Map<String, Object> details = new HashMap<>();
        details.put("worker", workerService);
        details.put("message", "Step execution started");

        AuditEvent event = AuditEvent.builder()
            .executionId(executionId)
            .orchName(orchName)
            .entityType(AuditEntityTypeEnum.STEP)
            .stepName(stepName)
            .eventType(AuditEventTypeEnum.STEP_STARTED)
            .timestamp(LocalDateTime.now())
            .serviceName(workerService)
            .operationType(operationType)
            .details(details)
            .build();

        recordEvent(event);
    }

    /**
     * Record step success event
     */
    public void recordStepSuccess(String executionId, String orchName, String stepName,
                                  String workerService, Long durationMs, String operationType, Integer retryCount) {
        Map<String, Object> details = new HashMap<>();
        details.put("worker", workerService);
        details.put("durationMs", durationMs);
        details.put("retryCount", retryCount);
        details.put("message", "Step completed successfully");

        AuditEvent event = AuditEvent.builder()
            .executionId(executionId)
            .orchName(orchName)
            .entityType(AuditEntityTypeEnum.STEP)
            .stepName(stepName)
            .eventType(AuditEventTypeEnum.STEP_SUCCESS)
            .status("SUCCESS")
            .timestamp(LocalDateTime.now())
            .serviceName(workerService)
            .operationType(operationType)
            .durationMs(durationMs)
            .retryCount(retryCount)
            .details(details)
            .build();

        recordEvent(event);
    }

    /**
     * Record step failure event
     */
    public void recordStepFailure(String executionId, String orchName, String stepName,
                                  String workerService, String reason, Integer retryCount, String operationType) {
        Map<String, Object> details = new HashMap<>();
        details.put("worker", workerService);
        details.put("retryCount", retryCount);
        details.put("message", "Step execution failed");

        AuditEvent event = AuditEvent.builder()
            .executionId(executionId)
            .orchName(orchName)
            .entityType(AuditEntityTypeEnum.STEP)
            .stepName(stepName)
            .eventType(AuditEventTypeEnum.STEP_FAILED)
            .status("FAILED")
            .timestamp(LocalDateTime.now())
            .reason(reason)
            .serviceName(workerService)
            .operationType(operationType)
            .retryCount(retryCount)
            .details(details)
            .build();

        recordEvent(event);
    }

    /**
     * Record retry attempt event
     */
    public void recordRetryAttempt(String executionId, String orchName, String stepName,
                                   Integer retryCount, Integer maxRetries, String operationType, Long backoffMs) {
        Map<String, Object> details = new HashMap<>();
        details.put("retryCount", retryCount);
        details.put("maxRetries", maxRetries);
        details.put("backoffMs", backoffMs);
        details.put("message", String.format("Retry attempt %d of %d", retryCount, maxRetries));

        AuditEvent event = AuditEvent.builder()
            .executionId(executionId)
            .orchName(orchName)
            .entityType(AuditEntityTypeEnum.STEP)
            .stepName(stepName)
            .eventType(AuditEventTypeEnum.STEP_RETRY_TRIGGERED)
            .timestamp(LocalDateTime.now())
            .retryCount(retryCount)
            .operationType(operationType)
            .details(details)
            .build();

        recordEvent(event);
    }

    /**
     * Record rollback triggered event
     */
    public void recordRollbackTriggered(String executionId, String orchName, String stepName, String reason) {
        Map<String, Object> details = new HashMap<>();
        details.put("message", "Rollback triggered for orchestration");
        details.put("triggerStep", stepName);

        AuditEvent event = AuditEvent.builder()
            .executionId(executionId)
            .orchName(orchName)
            .entityType(AuditEntityTypeEnum.ORCHESTRATION)
            .stepName(stepName)
            .eventType(AuditEventTypeEnum.ROLLBACK_TRIGGERED)
            .timestamp(LocalDateTime.now())
            .reason(reason)
            .details(details)
            .build();

        recordEvent(event);
    }

    /**
     * Record rollback started event - when rollback process actually begins
     */
    public void recordRollbackStarted(String executionId, String orchName, String reason, Integer stepsToRollback) {
        Map<String, Object> details = new HashMap<>();
        details.put("message", "Rollback process started");
        details.put("stepsToRollback", stepsToRollback);

        AuditEvent event = AuditEvent.builder()
            .executionId(executionId)
            .orchName(orchName)
            .entityType(AuditEntityTypeEnum.ORCHESTRATION)
            .eventType(AuditEventTypeEnum.ROLLBACK_STARTED)
            .timestamp(LocalDateTime.now())
            .reason(reason)
            .details(details)
            .build();

        recordEvent(event);
    }

    /**
     * Record UNDO start event
     */
    public void recordUndoStart(String executionId, String orchName, String stepName, String workerService) {
        Map<String, Object> details = new HashMap<>();
        details.put("worker", workerService);
        details.put("message", "UNDO operation started");

        AuditEvent event = AuditEvent.builder()
            .executionId(executionId)
            .orchName(orchName)
            .entityType(AuditEntityTypeEnum.STEP)
            .stepName(stepName)
            .eventType(AuditEventTypeEnum.UNDO_STARTED)
            .timestamp(LocalDateTime.now())
            .serviceName(workerService)
            .operationType("UNDO")
            .details(details)
            .build();

        recordEvent(event);
    }

    /**
     * Record UNDO completion event
     */
    public void recordUndoComplete(String executionId, String orchName, String stepName,
                                   String workerService, Long durationMs) {
        Map<String, Object> details = new HashMap<>();
        details.put("worker", workerService);
        details.put("durationMs", durationMs);
        details.put("message", "UNDO operation completed");

        AuditEvent event = AuditEvent.builder()
            .executionId(executionId)
            .orchName(orchName)
            .entityType(AuditEntityTypeEnum.STEP)
            .stepName(stepName)
            .eventType(AuditEventTypeEnum.UNDO_COMPLETED)
            .status("ROLLED_BACK")
            .timestamp(LocalDateTime.now())
            .serviceName(workerService)
            .operationType("UNDO")
            .durationMs(durationMs)
            .details(details)
            .build();

        recordEvent(event);
    }

    /**
     * Record UNDO failure event
     */
    public void recordUndoFailure(String executionId, String orchName, String stepName,
                                 String workerService, String reason) {
        Map<String, Object> details = new HashMap<>();
        details.put("worker", workerService);
        details.put("message", "UNDO operation failed");

        AuditEvent event = AuditEvent.builder()
            .executionId(executionId)
            .orchName(orchName)
            .entityType(AuditEntityTypeEnum.STEP)
            .stepName(stepName)
            .eventType(AuditEventTypeEnum.UNDO_FAILED)
            .status("FAILED")
            .timestamp(LocalDateTime.now())
            .reason(reason)
            .serviceName(workerService)
            .operationType("UNDO")
            .details(details)
            .build();

        recordEvent(event);
    }

    /**
     * Record rollback completion event
     */
    public void recordRollbackComplete(String executionId, String orchName, Integer rolledBackStepsCount) {
        Map<String, Object> details = new HashMap<>();
        details.put("message", "All steps rolled back successfully");
        details.put("rolledBackSteps", rolledBackStepsCount);

        AuditEvent event = AuditEvent.builder()
            .executionId(executionId)
            .orchName(orchName)
            .entityType(AuditEntityTypeEnum.ORCHESTRATION)
            .eventType(AuditEventTypeEnum.ROLLBACK_COMPLETED)
            .status("ROLLED_BACK")
            .timestamp(LocalDateTime.now())
            .details(details)
            .build();

        recordEvent(event);
    }

    // ===== Conversion Methods =====

    private AuditEventDto toDto(AuditEvent event) {
        return AuditEventDto.builder()
            .id(event.getId())
            .executionId(event.getExecutionId())
            .orchName(event.getOrchName())
            .entityType(event.getEntityType().name())
            .stepName(event.getStepName())
            .eventType(event.getEventType().name())
            .status(event.getStatus())
            .timestamp(event.getTimestamp())
            .reason(event.getReason())
            .details(event.getDetails())
            .createdBy(event.getCreatedBy())
            .serviceName(event.getServiceName())
            .operationType(event.getOperationType())
            .durationMs(event.getDurationMs())
            .retryCount(event.getRetryCount())
            .build();
    }
}

