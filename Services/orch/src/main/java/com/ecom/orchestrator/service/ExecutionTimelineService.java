package com.ecom.orchestrator.service;

import com.ecom.orchestrator.dto.TimelineEventDto;
import com.ecom.orchestrator.entity.AuditEvent;
import com.ecom.orchestrator.repository.AuditEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExecutionTimelineService {

    private final AuditEventRepository auditEventRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Build timeline series for an execution using audit events.
     */
    public List<TimelineEventDto> getTimeline(String executionId) {
        log.info("Building timeline for executionId: {}", executionId);
        List<AuditEvent> events = auditEventRepository.findByExecutionIdOrderByTimestampAsc(executionId);
        return events.stream().map(this::toTimelineEventDto).toList();
    }

    private TimelineEventDto toTimelineEventDto(AuditEvent event) {
        String detailsJson = null;
        if (event.getDetails() != null) {
            try {
                detailsJson = objectMapper.writeValueAsString(event.getDetails());
            } catch (JsonProcessingException e) {
                log.warn("Failed to serialize details map for event {}: {}", event.getId(), e.getMessage());
            }
        }
        return TimelineEventDto.builder()
                .timestamp(event.getTimestamp())
                .event(event.getEventType() != null ? event.getEventType().name() : null)
                .step(event.getStepName())
                .status(event.getStatus())
                .reason(event.getReason())
                .details(detailsJson)
                .build();
    }
}
