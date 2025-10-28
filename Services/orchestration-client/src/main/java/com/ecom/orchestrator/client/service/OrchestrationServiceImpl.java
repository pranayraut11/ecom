package com.ecom.orchestrator.client.service;

import com.ecom.orchestrator.client.config.OrchestrationConfig;
import com.ecom.orchestrator.client.dto.ExecutionMessage;
import com.ecom.orchestrator.client.enums.STATUS;
import com.ecom.orchestrator.client.publisher.KafkaEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrchestrationServiceImpl implements OrchestrationService {

    private final KafkaEventPublisher kafkaEventPublisher;
    private final ObjectMapper yamlObjectMapper;
    private final ObjectMapper jsonObjectMapper;

    @Value("${spring.application.name:OrchestrationService}")
    private String applicationName;

    @Override
    public void startOrchestration(ExecutionMessage data, String orchestratorName, String topicName) {
        try {
            log.info("Starting orchestration with data length:  bytes to topic: {}",
                     topicName);
            // Step 5: Send event to Kafka
            kafkaEventPublisher.publishEvent(createMessageWithAdditionalHeaders(data,orchestratorName,topicName));

            log.info("Successfully sent start orchestration event for topic: {}",
                    topicName);


        } catch (Exception e) {
            log.error("Failed to send start orchestration event for topic: {}", topicName, e);
            throw new RuntimeException("Orchestration start failed", e);
        }
    }

    private ExecutionMessage createMessageWithAdditionalHeaders(ExecutionMessage originalMessage, String orchestratorName, String topicName) {
        HashMap<String,Object> headers = new HashMap<>(originalMessage.getHeaders());
        headers.put("orchestrationName", orchestratorName);
        headers.put("eventType", "ORCHESTRATION_STARTED");
        headers.put("flowId", UUID.randomUUID().toString());
        headers.put("source", applicationName);
        headers.put("topic", topicName);
        headers.put("serviceName", applicationName);
        headers.put("X-Service-Name", applicationName);
        originalMessage.getHeaders().putAll(headers);
       return originalMessage;
    }

    @Override
    public void startOrchestration(ExecutionMessage data, String orchestratorName) {
        startOrchestration(data, orchestratorName,"orchestrator.execution.start");
    }

    @Override
    public void register(OrchestrationConfig.Orchestration config, String topicName) {
        try {
            log.info(" Register orchestration  topic: {}",topicName);

            // Step 1: Parse the input data (YAML or JSON)


                log.info("Parsed orchestration config: {}", config.getOrchestrationName());
                // Step 2: Validate the configuration
                validateOrchestrationConfig(config);

                // Step 3: Check Kafka connection
                validateKafkaConnection();

                // Step 4: Create message with headers and payload
                ExecutionMessage message = createOrchestrationRegistrationMessage(config, topicName);

                // Step 5: Send event to Kafka
                kafkaEventPublisher.publishEvent(message);
                log.info("Successfully sent orchestration registration: {} for topic: {}",
                        config.getOrchestrationName(), topicName);

        } catch (Exception e) {
            log.error("Failed to start orchestration for topic: {}", topicName, e);
            throw new RuntimeException("Orchestration start failed", e);
        }

    }

    @Override
    public void register(OrchestrationConfig.Orchestration config) {
        register(config, "orchestrator.registration");
    }

    @Override
    public void sendSuccessResponse(ExecutionMessage message, String topicName) {
        try {
            log.info("Sending success response to topic: {}", topicName);

            // Create success response message with additional headers
            ExecutionMessage successMessage = createResponseMessage(message, topicName, STATUS.SUCCESS);

            // Send event to Kafka
            kafkaEventPublisher.publishEvent(successMessage);

            log.info("Successfully sent success response to topic: {}", topicName);
        } catch (Exception e) {
            log.error("Failed to send success response to topic: {}", topicName, e);
            throw new RuntimeException("Failed to send success response", e);
        }
    }

    @Override
    public void sendSuccessResponse(ExecutionMessage message) {
        sendSuccessResponse(message, "orchestrator.response.result");
    }

    @Override
    public void sendFailureResponse(ExecutionMessage message, String topicName) {
        try {
            log.info("Sending failure response to topic: {}", topicName);

            // Create failure response message with additional headers
            ExecutionMessage failureMessage = createResponseMessage(message, topicName, STATUS.FAILURE);

            // Send event to Kafka
            kafkaEventPublisher.publishEvent(failureMessage);

            log.info("Successfully sent failure response to topic: {}", topicName);
        } catch (Exception e) {
            log.error("Failed to send failure response to topic: {}", topicName, e);
            throw new RuntimeException("Failed to send failure response", e);
        }
    }

    @Override
    public void sendFailureResponse(ExecutionMessage message) {
        sendFailureResponse(message, "orchestrator.execution.failure");
    }


    /**
     * Create orchestration message with proper headers and payload
     */
    private ExecutionMessage createOrchestrationRegistrationMessage(OrchestrationConfig.Orchestration config, String topicName) {
        try {
            // Create payload from orchestration config

            HashMap <String, Object> headers = new HashMap<>();
            // Create message with headers
            headers.put("flowId", UUID.randomUUID().toString());
            headers.put("eventType", "ORCHESTRATION_STARTED");
            headers.put("orchestrationName", config.getOrchestrationName());
            headers.put("source", applicationName);
            headers.put("topic", topicName);
            headers.put("contentType", "application/json");
            headers.put("X-Service-Name", applicationName);
            return ExecutionMessage.builder().payload(config).headers(headers).build();


        } catch (Exception e) {
            throw new RuntimeException("Failed to create orchestration message", e);
        }
    }

    private Message<?> createOrchestrationStartMessage(Object data,String orchestrationName, String topicName) {
        try {
            // Create payload from orchestration config
            String jsonPayload = jsonObjectMapper.writeValueAsString(data);

            // Create message with headers
            return MessageBuilder
                    .withPayload(jsonPayload)
                    .setHeader("flowId", UUID.randomUUID().toString())
                    .setHeader("eventType", "ORCHESTRATION_STARTED")
                    .setHeader("orchestrationName", orchestrationName)
                    .setHeader("source", applicationName)
                    .setHeader("topic", topicName)
                    .setHeader("contentType", "application/json")
                    .setHeader("X-Service-Name", applicationName)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to create orchestration message", e);
        }
    }

    /**
     * Validate orchestration configuration
     */
    private void validateOrchestrationConfig(OrchestrationConfig.Orchestration config) {
        if (config.getOrchestrationName() == null || config.getOrchestrationName().trim().isEmpty()) {
            throw new RuntimeException("Orchestration name (orchName) is required");
        }

        if (config.getSteps() == null || config.getSteps().isEmpty()) {
            throw new RuntimeException("At least one step is required");
        }

        // Validate each step
        config.getSteps().forEach(step -> {
            if (config.getAs().equalsIgnoreCase("initiator") && step.getSeq() == null) {
                throw new RuntimeException("Step sequence (seq) is required");
            }
            if (step.getName() == null || step.getName().trim().isEmpty()) {
                throw new RuntimeException("Step name is required");
            }
        });

        log.info("Orchestration config validation passed for: {}", config.getOrchestrationName());
    }

    /**
     * Validate Kafka connection health
     */
    private void validateKafkaConnection() {
        if (!kafkaEventPublisher.isConnectionHealthy()) {
            throw new RuntimeException("Kafka connection is not healthy");
        }
        log.info("Kafka connection validated successfully");
    }


    /**
     * Create response message with proper headers and status
     */
    private ExecutionMessage createResponseMessage(ExecutionMessage originalMessage, String topicName, STATUS status) {
        try {
            // Create new headers based on original message headers
            HashMap<String, Object> headers = new HashMap<>(originalMessage.getHeaders());

            // Add response-specific headers
            headers.put("eventType", "ORCHESTRATION_" + status);
            headers.put("status", status.isValue());
            headers.put("source", applicationName);
            headers.put("topic", topicName);
            headers.put("contentType", "application/json");
            headers.put("X-Service-Name", applicationName);

            // Preserve original orchestration context if present
            if (originalMessage.getHeaders().containsKey("orchestrationName")) {
                headers.put("orchestrationName", originalMessage.getHeaders().get("orchestrationName"));
            }


            return ExecutionMessage.builder()
                    .payload(originalMessage.getPayload())
                    .headers(headers)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to create response message", e);
        }
    }
}
