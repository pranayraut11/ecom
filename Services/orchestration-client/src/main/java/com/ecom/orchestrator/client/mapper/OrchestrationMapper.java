package com.ecom.orchestrator.client.mapper;

import com.ecom.orchestrator.client.config.OrchestrationConfig;
import com.ecom.orchestrator.client.dto.OrchestrationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.messaging.support.MessageBuilder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * MapStruct mapper for converting between orchestration objects and messages
 */
@Mapper(componentModel = "spring", imports = {UUID.class, LocalDateTime.class, MessageBuilder.class})
public interface OrchestrationMapper {
    /**
     * Creates OrchestrationEvent from OrchestrationDefinition and topic
     */
    @Mapping(target = "flowId", expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "topic", source = "topicName")
    @Mapping(target = "orchestrationName", source = "definition.orchestrationName")
    @Mapping(target = "eventType", constant = "ORCHESTRATION_STARTED")
    @Mapping(target = "timestamp", expression = "java(LocalDateTime.now())")
    @Mapping(target = "source", constant = "OrchestrationService")
    @Mapping(target = "payload", source = "definition", qualifiedByName = "createPayload")
    OrchestrationEvent toOrchestrationEvent(OrchestrationConfig.Orchestration definition, String topicName);

    /**
     * Creates payload map from OrchestrationDefinition
     */
    @Named("createPayload")
    default Map<String, Object> createPayload(OrchestrationConfig.Orchestration definition) {
        return Map.of(
            "orchestrationName", definition.getOrchestrationName(),
            "type", definition.getType() != null ? definition.getType() : "sequential",
            "as", definition.getAs() != null ? definition.getAs() : "initiator",
            "steps", definition.getSteps() != null ? definition.getSteps() : Collections.emptyList()
        );
    }

    /**
     * Creates Spring Message with headers from OrchestrationEvent
     */
    @Mapping(target = "payload", source = "event", qualifiedByName = "eventToJsonString")
    @Mapping(target = "headers", source = "event", qualifiedByName = "createHeaders")
    MessageData toMessageData(OrchestrationEvent event);

    /**
     * Helper method to convert event payload to JSON string
     */
    @Named("eventToJsonString")
    default String eventToJsonString(OrchestrationEvent event) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(event.getPayload());
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert payload to JSON", e);
        }
    }

    /**
     * Creates message headers from OrchestrationEvent
     */
    @Named("createHeaders")
    default Map<String, Object> createHeaders(OrchestrationEvent event) {
        return Map.of(
            "flowId", event.getFlowId(),
            "eventType", event.getEventType(),
            "orchestrationName", event.getOrchestrationName(),
            "source", event.getSource(),
            "timestamp", event.getTimestamp(),
            "topic", event.getTopic(),
            "contentType", "application/json",
            "X-Service-Name", event.getSource()
        );
    }

    /**
     * Data class for message creation
     */
    class MessageData {
        private String payload;
        private Map<String, Object> headers;

        public MessageData() {}

        public MessageData(String payload, Map<String, Object> headers) {
            this.payload = payload;
            this.headers = headers;
        }

        public String getPayload() { return payload; }
        public void setPayload(String payload) { this.payload = payload; }
        public Map<String, Object> getHeaders() { return headers; }
        public void setHeaders(Map<String, Object> headers) { this.headers = headers; }
    }
}
