package com.ecom.orchestrator.serialization;

import com.ecom.orchestrator.dto.ExecutionMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class ExecutionMessageDeserializer implements Deserializer<ExecutionMessage> {

    private final ObjectMapper objectMapper;

    public ExecutionMessageDeserializer() {
        this.objectMapper = new ObjectMapper();
    }

    public ExecutionMessageDeserializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ExecutionMessage deserialize(String topic, byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        try {
            return objectMapper.readValue(data, ExecutionMessage.class);
        } catch (IOException e) {
            throw new SerializationException("Error deserializing ExecutionMessage from topic: " + topic, e);
        }
    }

    @Override
    public void close() {
        // No resources to close
    }
}
