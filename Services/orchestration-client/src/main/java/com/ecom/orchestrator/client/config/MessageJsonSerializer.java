package com.ecom.orchestrator.client.config;

import com.ecom.orchestrator.client.dto.ExecutionMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Custom JSON serializer for Spring Message objects
 */
public class MessageJsonSerializer implements Serializer<ExecutionMessage> {

    private final ObjectMapper objectMapper;

    public MessageJsonSerializer() {
        this.objectMapper = new ObjectMapper();
    }

    public MessageJsonSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // No configuration needed
    }

    @Override
    public byte[] serialize(String topic, ExecutionMessage message) {
        if (message == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsBytes(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize Message to JSON bytes", e);
        }
    }

    @Override
    public void close() {
        // No resources to close
    }
}
