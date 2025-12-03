package com.ecom.orchestrator.client.service;

import com.ecom.orchestrator.client.config.OrchestrationConfig;
import com.ecom.orchestrator.client.dto.ExecutionMessage;
import com.ecom.orchestrator.client.enums.STATUS;
import com.ecom.orchestrator.client.publisher.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.ecom.orchestrator.client.constants.Constant.ORCHESTRATOR_EXECUTION_START;
import static com.ecom.orchestrator.client.constants.Constant.ORCHESTRATOR_REGISTRATION;
import static com.ecom.orchestrator.client.constants.Constant.ORCHESTRATOR_RESPONSE_RESULT;

/**
 * Service implementation for handling single events using shared topic
 * Only activated when sharedTopic is enabled in orchestrations.yml
 */
@ConditionalOnProperty(name = "shared.topic", havingValue = "true")
@Slf4j
@Service
@RequiredArgsConstructor
public class SingleEventServiceImpl implements OrchestrationService {

    private final KafkaEventPublisher kafkaEventPublisher;

    @Value("${spring.application.name:SingleEventService}")
    private String applicationName;

    private static final String DEFAULT_EVENT_TOPIC = "orchestrator.event";
    private static final String HEADER_EVENT_TYPE = "eventType";
    private static final String HEADER_FLOW_ID = "flowId";
    private static final String HEADER_SOURCE = "source";
    private static final String HEADER_SERVICE_NAME = "serviceName";
    private static final String HEADER_X_SERVICE_NAME = "X-Service-Name";
    private static final String HEADER_TOPIC = "topic";
    private static final String HEADER_CONTENT_TYPE = "contentType";
    private static final String HEADER_STATUS = "status";
    private static final String HEADER_RESPONSE_TYPE = "responseType";

    @Override
    public void startOrchestration(ExecutionMessage data, String orchestratorName, String topicName) {
        try {
            log.info("Starting single event orchestration: {} on topic: {}", orchestratorName, topicName);

            // Add orchestration metadata to headers
            if (!data.getHeaders().containsKey(HEADER_EVENT_TYPE)) {
                data.getHeaders().put(HEADER_EVENT_TYPE, ORCHESTRATOR_EXECUTION_START);
            }

            // Add flowId if not present
            if (!data.getHeaders().containsKey(HEADER_FLOW_ID)) {
                data.getHeaders().put(HEADER_FLOW_ID, UUID.randomUUID().toString());
            }

            // Add orchestration name
            data.getHeaders().put("orchestrationName", orchestratorName);

            // Add source information
            data.getHeaders().put(HEADER_SOURCE, applicationName);
            data.getHeaders().put(HEADER_SERVICE_NAME, applicationName);
            data.getHeaders().put(HEADER_X_SERVICE_NAME, applicationName);

            // Enrich and send
            enrichMessageHeaders(data, topicName);
            kafkaEventPublisher.publishEvent(data);

            log.info("Successfully started orchestration with flowId: {}", data.getHeaders().get(HEADER_FLOW_ID));

        } catch (Exception e) {
            log.error("Failed to start orchestration: {}", orchestratorName, e);
            throw new IllegalStateException("Orchestration start failed", e);
        }
    }

    @Override
    public void startOrchestration(ExecutionMessage data, String orchestratorName) {
        startOrchestration(data, orchestratorName, DEFAULT_EVENT_TOPIC);
    }

    @Override
    public void register(OrchestrationConfig.Orchestration data, String topicName) {
        try {
            log.info("Registering orchestration: {} on topic: {}", data.getOrchestrationName(), topicName);

            // Create execution message for registration
            HashMap<String, Object> headers = new HashMap<>();
            headers.put(HEADER_EVENT_TYPE, ORCHESTRATOR_REGISTRATION);
            headers.put(HEADER_FLOW_ID, UUID.randomUUID().toString());
            headers.put("orchestrationName", data.getOrchestrationName());
            headers.put(HEADER_SOURCE, applicationName);
            headers.put(HEADER_SERVICE_NAME, applicationName);
            headers.put(HEADER_X_SERVICE_NAME, applicationName);
            headers.put(HEADER_TOPIC, topicName);
            headers.put(HEADER_CONTENT_TYPE, "application/json");

            ExecutionMessage message = ExecutionMessage.builder()
                    .payload(data)
                    .headers(headers)
                    .build();

            kafkaEventPublisher.publishEvent(message);

            log.info("Successfully registered orchestration: {}", data.getOrchestrationName());

        } catch (Exception e) {
            log.error("Failed to register orchestration: {}", data.getOrchestrationName(), e);
            throw new IllegalStateException("Orchestration registration failed", e);
        }
    }

    @Override
    public void register(OrchestrationConfig.Orchestration data) {
        register(data, DEFAULT_EVENT_TOPIC);
    }

    @Override
    public void doNext(ExecutionMessage message, String topicName) {
        try {
            log.info("Sending doNext event to topic: {}", topicName);

            // Ensure message has required headers
            enrichMessageHeaders(message, topicName);

            // Update event type
            message.getHeaders().put(HEADER_EVENT_TYPE, ORCHESTRATOR_RESPONSE_RESULT);

            // Send event to Kafka
            kafkaEventPublisher.publishEvent(message);

            log.info("Successfully sent doNext event to topic: {}", topicName);

        } catch (Exception e) {
            log.error("Failed to send doNext event to topic: {}", topicName, e);
            throw new IllegalStateException("DoNext event send failed", e);
        }
    }

    @Override
    public void doNext(ExecutionMessage message) {
        doNext(message, DEFAULT_EVENT_TOPIC);
    }

    @Override
    public void undoNext(ExecutionMessage message, String topicName) {
        try {
            log.info("Sending undoNext event to topic: {}", topicName);

            // Create failure response message
            ExecutionMessage undoMessage = createResponseMessage(message, topicName, STATUS.FAILURE, false);
            undoMessage.getHeaders().put(HEADER_EVENT_TYPE, ORCHESTRATOR_RESPONSE_RESULT);

            // Send event to Kafka
            kafkaEventPublisher.publishEvent(undoMessage);

            log.info("Successfully sent undoNext event to topic: {}", topicName);

        } catch (Exception e) {
            log.error("Failed to send undoNext event to topic: {}", topicName, e);
            throw new IllegalStateException("UndoNext event send failed", e);
        }
    }

    @Override
    public void undoNext(ExecutionMessage message) {
        undoNext(message, DEFAULT_EVENT_TOPIC);
    }

    @Override
    public void failStep(ExecutionMessage message) {
        failStep(message, DEFAULT_EVENT_TOPIC);
    }

    @Override
    public void failStep(ExecutionMessage message, String topicName) {
        try {
            log.info("Sending failStep event to topic: {}", topicName);

            // Create failure response message
            ExecutionMessage failureMessage = createResponseMessage(message, topicName, STATUS.FAILURE, false);
            failureMessage.getHeaders().put(HEADER_EVENT_TYPE, ORCHESTRATOR_RESPONSE_RESULT);
            failureMessage.getHeaders().put("action","FAIL_STEP");
            kafkaEventPublisher.publishEvent(failureMessage);

            log.info("Successfully sent failStep event to topic: {}", ORCHESTRATOR_RESPONSE_RESULT);

        } catch (Exception e) {
            log.error("Failed to send failStep event to topic: {}", topicName, e);
            throw new IllegalStateException("FailStep event send failed", e);
        }
    }

    /**
     * Enrich message headers with required metadata
     */
    private void enrichMessageHeaders(ExecutionMessage message, String topicName) {
        Map<String, Object> headers = message.getHeaders();

        // Add flowId if not present
        if (!headers.containsKey(HEADER_FLOW_ID)) {
            headers.put(HEADER_FLOW_ID, UUID.randomUUID().toString());
        }

        // Add source information
        headers.put(HEADER_STATUS, true);
        headers.put(HEADER_SOURCE, applicationName);
        headers.put(HEADER_TOPIC, topicName);
        headers.put(HEADER_SERVICE_NAME, applicationName);
        headers.put(HEADER_X_SERVICE_NAME, applicationName);
        headers.put(HEADER_CONTENT_TYPE, "application/json");
    }

    /**
     * Create response message with proper headers and status
     */
    private ExecutionMessage createResponseMessage(ExecutionMessage originalMessage, String topicName, STATUS status, boolean isSuccess) {
        try {
            // Create new headers based on original message headers
            HashMap<String, Object> headers = new HashMap<>(originalMessage.getHeaders());

            // Get existing status or use default
            Object statusObj = originalMessage.getHeaders().get(HEADER_STATUS);

            // Add response-specific headers
            headers.put(HEADER_EVENT_TYPE, "EVENT_" + status);
            headers.put(HEADER_STATUS, Objects.nonNull(statusObj) ? statusObj : isSuccess);
            headers.put(HEADER_SOURCE, applicationName);
            headers.put(HEADER_TOPIC, topicName);
            headers.put(HEADER_CONTENT_TYPE, "application/json");
            headers.put(HEADER_X_SERVICE_NAME, applicationName);
            headers.put(HEADER_RESPONSE_TYPE, isSuccess ? "SUCCESS" : "FAILURE");

            // Preserve flowId
            if (!headers.containsKey(HEADER_FLOW_ID)) {
                headers.put(HEADER_FLOW_ID, UUID.randomUUID().toString());
            }

            return ExecutionMessage.builder()
                    .payload(originalMessage.getPayload())
                    .headers(headers)
                    .build();

        } catch (Exception e) {
            throw new IllegalStateException("Failed to create response message", e);
        }
    }
}

