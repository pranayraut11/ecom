package com.ecom.orchestrator.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrchestrationEvent {

    private String flowId;
    private String topic;
    private String orchestrationName;
    private String eventType;
    private Map<String, Object> payload;
    private LocalDateTime timestamp;
    private String source;

    public OrchestrationEvent(String topic, String orchestrationName, String eventType, Map<String, Object> payload) {
        this.topic = topic;
        this.orchestrationName = orchestrationName;
        this.eventType = eventType;
        this.payload = payload;
        this.timestamp = LocalDateTime.now();
        this.flowId = java.util.UUID.randomUUID().toString();
    }
}
