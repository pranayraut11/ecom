package com.ecom.orchestrator.service;

import com.ecom.orchestrator.dto.ExecutionMessage;
import com.ecom.orchestrator.dto.OrchestrationEventDto;
import com.ecom.orchestrator.dto.OrchestrationRegistrationDto;
import com.ecom.orchestrator.messaging.interfaces.MessageHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class OrchestrationMessageHandler implements MessageHandler {

    private final OrchestrationRegistryService registryService;
    private final OrchestrationExecutorService executorService;
    private final UndoService undoService;
    private final ObjectMapper objectMapper;

    public OrchestrationMessageHandler(
            OrchestrationRegistryService registryService,
            OrchestrationExecutorService executorService,
            UndoService undoService,
            ObjectMapper objectMapper) {
        this.registryService = registryService;
        this.executorService = executorService;
        this.undoService = undoService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(String topic, ExecutionMessage message) {
        log.debug("Received message on topic: {} with headers: {}", topic, message.getHeaders());

        try {
            if (topic.equals("orchestrator.registration")) {
                handleRegistrationMessage(message);
            } else if (topic.equals("orchestrator.execution.start")) {
                handleExecutionStartMessage(message);
            } else if (topic.contains("orchestrator.") && (topic.contains(".response") || topic.contains(".result"))) {
                handleStepResponseMessage(message);
            } else {
                log.warn("Unknown topic: {}", topic);
            }
        } catch (Exception e) {
            log.error("Error processing message from topic: {}", topic, e);
        }
    }

    private void handleRegistrationMessage(ExecutionMessage message) {
        try {
            OrchestrationRegistrationDto registration = parseAndValidateRegistration(message);
            if (registration == null) {
                log.warn("Failed to parse registration from message, skipping registration");
                return;
            }

            String serviceName = extractServiceName(message, registration);
            registerOrchestration(registration, serviceName);

        } catch (Exception e) {
            log.error("Error handling registration message", e);
        }
    }

    /**
     * Parse and validate the registration DTO from the message payload
     *
     * @param message the incoming message
     * @return parsed OrchestrationRegistrationDto or null if parsing fails
     */
    private OrchestrationRegistrationDto parseAndValidateRegistration(ExecutionMessage message) {
        try {
            OrchestrationRegistrationDto registration = parseRegistrationFromMessage(message);
            if (registration != null && isValidRegistration(registration)) {
                log.debug("Successfully parsed registration for orchestration: {}",
                    registration.getOrchestrationName());
                return registration;
            }
            return null;
        } catch (Exception e) {
            log.error("Exception occurred while parsing registration message", e);
            return null;
        }
    }

    /**
     * Validate the registration DTO contains required fields
     *
     * @param registration the registration DTO to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidRegistration(OrchestrationRegistrationDto registration) {
        if (registration.getOrchestrationName() == null || registration.getOrchestrationName().trim().isEmpty()) {
            log.error("Registration message missing required orchestration name");
            return false;
        }

        if (registration.getSteps() == null || registration.getSteps().isEmpty()) {
            log.error("Registration message missing required steps for orchestration: {}",
                registration.getOrchestrationName());
            return false;
        }

        return true;
    }

    /**
     * Register the orchestration with the registry service
     *
     * @param registration the registration DTO
     * @param serviceName the service name
     */
    private void registerOrchestration(OrchestrationRegistrationDto registration, String serviceName) {
        try {
            registryService.registerOrchestration(registration, serviceName);
            log.info("Successfully registered orchestration '{}' for service '{}'",
                registration.getOrchestrationName(), serviceName);
        } catch (Exception e) {
            log.error("Failed to register orchestration '{}' for service '{}': {}",
                registration.getOrchestrationName(), serviceName, e.getMessage(), e);
            throw e; // Re-throw to be handled by caller
        }
    }

    /**
     * Parse OrchestrationRegistrationDto from the incoming message payload
     */
    private OrchestrationRegistrationDto parseRegistrationFromMessage(ExecutionMessage message) {
        if (message.getPayload() instanceof HashMap map) {
            return objectMapper.convertValue(map, OrchestrationRegistrationDto.class);
        } else {
            log.error("Invalid payload type for registration message: {}", message.getPayload().getClass());
            return null;
        }
    }

    /**
     * Parse OrchestrationRegistrationDto from byte array payload
     * Handles both direct format and nested payload structure
     */
    private OrchestrationRegistrationDto parseRegistrationFromBytes(byte[] payload) {
        try {
            // First attempt: try direct deserialization
            return objectMapper.readValue(payload, OrchestrationRegistrationDto.class);

        } catch (UnrecognizedPropertyException e) {
            if (e.getMessage().contains("payload")) {
                return extractRegistrationFromNestedPayload(payload);
            } else {
                log.info("Unrecognized property in registration message, possible version mismatch: {}", e.getMessage());
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to deserialize registration message", e);
            return null;
        }
    }

    /**
     * Extract registration data from nested payload structure
     * Handles cases where actual data is wrapped in a "payload" field
     */
    private OrchestrationRegistrationDto extractRegistrationFromNestedPayload(byte[] payload) {
        log.debug("Message has nested payload structure, extracting inner payload");

        try {
            Map<String, Object> messageWrapper = objectMapper.readValue(payload, Map.class);
            Object nestedPayload = messageWrapper.get("payload");

            if (nestedPayload instanceof String str) {
                return parseRegistrationFromJsonString(str);
            } else if (nestedPayload instanceof Map map) {
                return convertMapToRegistration(map);
            } else {
                log.error("Nested payload is neither String nor Map: {}",
                        nestedPayload != null ? nestedPayload.getClass() : "null");
                return null;
            }

        } catch (Exception e) {
            log.error("Failed to extract registration from nested payload structure", e);
            return null;
        }
    }

    /**
     * Parse OrchestrationRegistrationDto from a JSON string
     */
    private OrchestrationRegistrationDto parseRegistrationFromJsonString(String jsonString) {
        try {
            OrchestrationRegistrationDto registration = objectMapper.readValue(jsonString, OrchestrationRegistrationDto.class);
            log.debug("Successfully extracted registration from nested JSON string payload");
            return registration;
        } catch (Exception e) {
            log.error("Failed to parse registration from JSON string: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Convert Map object to OrchestrationRegistrationDto
     */
    private OrchestrationRegistrationDto convertMapToRegistration(Map<?, ?> payloadMap) {
        try {
            OrchestrationRegistrationDto registration = objectMapper.convertValue(payloadMap, OrchestrationRegistrationDto.class);
            log.debug("Successfully converted nested payload Map to registration");
            return registration;
        } catch (Exception e) {
            log.error("Failed to convert Map to registration: {}", e.getMessage());
            return null;
        }
    }

    private void handleExecutionStartMessage(ExecutionMessage message) {
        try {

            String orchName = message.getHeaders().get("orchestrationName").toString();
            executorService.startOrchestration(orchName,message);
        } catch (Exception e) {
            log.error("Error handling execution start message", e);
        }
    }

    private void handleStepResponseMessage(ExecutionMessage message) {
        try {
            String flowId = message.getHeaders().get("flowId").toString();
            String stepName = message.getHeaders().get("stepName").toString();
            boolean success = determineSuccessFromEvent(message);
            String errorMessage = extractErrorMessage(message);

            if ("UNDO".equals(message.getHeaders().get("action"))) {
                undoService.handleUndoResponse(flowId, stepName, success, errorMessage);
            } else {
                executorService.handleStepResponse(flowId, stepName, success, errorMessage,message);
            }
        } catch (Exception e) {
            log.error("Error handling step response message", e);
        }
    }

    private String extractServiceName(ExecutionMessage message, OrchestrationRegistrationDto registration) {
        // Try to get service name from message headers first
        Object serviceNameHeader = message.getHeaders().get("serviceName");
        if (serviceNameHeader != null) {
            return serviceNameHeader.toString();
        }

        // Try X-Service-Name header (like the controller does)
        Object xServiceNameHeader = message.getHeaders().get("X-Service-Name");
        if (xServiceNameHeader != null) {
            return xServiceNameHeader.toString();
        }

        // Fallback to orchestration name + "-service" (same logic as controller)
        return registration.getOrchestrationName() != null
                ? registration.getOrchestrationName() + "-service"
                : "unknown-service";
    }

    private boolean determineSuccessFromEvent(ExecutionMessage message) {
        // Check headers for success indicator
        Object successHeader = message.getHeaders().get("status");
        if (successHeader instanceof Boolean isSuccess) {
            return isSuccess;
        }
        if (successHeader instanceof String isSuccessStr) {
            return Boolean.parseBoolean(isSuccessStr);
        }
        // Default logic based on event content
        return false;
    }

    private String extractErrorMessage(ExecutionMessage message) {
        // Check headers for error message
        Object errorHeader = message.getHeaders().get("errorMessage");
        if (errorHeader != null) {
            return errorHeader.toString();
        }
        return null;
    }
}
