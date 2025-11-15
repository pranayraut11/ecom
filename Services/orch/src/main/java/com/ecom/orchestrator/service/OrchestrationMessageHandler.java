package com.ecom.orchestrator.service;

import com.ecom.orchestrator.dto.ExecutionMessage;
import com.ecom.orchestrator.dto.OrchestrationRegistrationDto;
import com.ecom.orchestrator.messaging.interfaces.MessageHandler;
import com.ecom.orchestrator.util.MessageHeaderUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class OrchestrationMessageHandler implements MessageHandler {

    private final OrchestrationRegistryService registryService;
    private final OrchestrationExecutorService executorService;
    private final DoOperationHandler doOperationHandler;
    private final UndoOperationHandler undoOperationHandler;
    private final ObjectMapper objectMapper;

    public OrchestrationMessageHandler(
            OrchestrationRegistryService registryService,
            OrchestrationExecutorService executorService,
            DoOperationHandler doOperationHandler,
            UndoOperationHandler undoOperationHandler,
            ObjectMapper objectMapper) {
        this.registryService = registryService;
        this.executorService = executorService;
        this.doOperationHandler = doOperationHandler;
        this.undoOperationHandler = undoOperationHandler;
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
            if (Objects.nonNull(message.getHeaders())) {
                Map<String, Object> headers = message.getHeaders();
                String flowId = MessageHeaderUtils.getString(headers, "flowId");
                String stepName = MessageHeaderUtils.getString(headers, "stepName");
                boolean success = determineSuccessFromEvent(message);
                String errorMessage = extractErrorMessage(message);
                String action = MessageHeaderUtils.getString(headers, "action");

                log.info("Received step response: flowId={}, stepName={}, action={}, success={}",
                        flowId, stepName, action, success);

                if ("FAIL_STEP".equalsIgnoreCase(action)) {
                    log.info("Handling FAIL_STEP action for flowId={}, stepName={}", flowId, stepName);
                    // For FAIL_STEP, we treat it as an undo operation
                    undoOperationHandler.handleFailResponse(flowId, stepName, success, errorMessage, message);
                } else if ("UNDO".equalsIgnoreCase(action)) {
                    undoOperationHandler.handleUndoResponse(flowId, stepName, success, errorMessage, message);
                }else {
                    // Backward compatibility - treat as DO operation
                    log.warn("No action specified, treating as DO operation");
                    doOperationHandler.handleDoResponse(flowId, stepName, success, errorMessage, message);
                }
            }
        } catch (Exception e) {
            log.error("Error handling step response message", e);
        }
    }

    private String extractServiceName(ExecutionMessage message, OrchestrationRegistrationDto registration) {
        Map<String, Object> headers = message.getHeaders();

        // Try to get service name from message headers first
        String serviceName = MessageHeaderUtils.getString(headers, "serviceName");
        if (!serviceName.isEmpty()) {
            return serviceName;
        }

        // Try X-Service-Name header (like the controller does)
        String xServiceName = MessageHeaderUtils.getString(headers, "X-Service-Name");
        if (!xServiceName.isEmpty()) {
            return xServiceName;
        }

        // Fallback to orchestration name + "-service" (same logic as controller)
        return registration.getOrchestrationName() != null
                ? registration.getOrchestrationName() + "-service"
                : "unknown-service";
    }

    private boolean determineSuccessFromEvent(ExecutionMessage message) {
        Map<String, Object> headers = message.getHeaders();

        // Check headers for success indicator
        if (MessageHeaderUtils.hasValue(headers, "status")) {
            return MessageHeaderUtils.getBoolean(headers, "status", false);
        }

        // Default logic based on event content
        return false;
    }

    private String extractErrorMessage(ExecutionMessage message) {
        Map<String, Object> headers = message.getHeaders();

        // Check headers for error message
        return MessageHeaderUtils.getString(headers, "errorMessage", null);
    }
}
