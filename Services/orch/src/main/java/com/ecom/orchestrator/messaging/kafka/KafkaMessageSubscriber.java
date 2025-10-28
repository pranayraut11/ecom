package com.ecom.orchestrator.messaging.kafka;

import com.ecom.orchestrator.dto.ExecutionMessage;
import com.ecom.orchestrator.dto.KafkaMessageDto;
import com.ecom.orchestrator.messaging.interfaces.MessageHandler;
import com.ecom.orchestrator.messaging.interfaces.MessageSubscriber;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageSubscriber implements MessageSubscriber {

    // Fallback constants for ErrorHandlingDeserializer headers
    private static final String VALUE_DESERIALIZER_EXCEPTION_HEADER = "springDeserializerExceptionValue";
    private static final String KEY_DESERIALIZER_EXCEPTION_HEADER = "springDeserializerExceptionKey";

    private final Map<String, MessageHandler> handlers = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    @Override
    public void subscribe(String topic, MessageHandler handler) {
        handlers.put(topic, handler);
        log.info("Subscribed to topic: {} with handler: {}", topic, handler.getClass().getSimpleName());
    }

    @KafkaListener(topics = "#{kafkaTopicConfig.getAllTopics()}", groupId = "orchestrator-service")
    public void listen(@Payload ExecutionMessage event,
                      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                      org.springframework.messaging.MessageHeaders messageHeaders,
                      org.springframework.kafka.support.Acknowledgment acknowledgment) {

        log.info("[KafkaMessageSubscriber] Received message from topic: {}", topic);
        try {
            log.info("[KafkaMessageSubscriber] Checking for deserialization error for topic: {}", topic);
            // Check if there was a deserialization error
            if (hasDeserializationError(messageHeaders)) {
                log.info("[KafkaMessageSubscriber] Deserialization error detected for topic: {}", topic);
                handleDeserializationError(topic, messageHeaders);
                return; // Discard the message and continue processing
            }

            log.info("[KafkaMessageSubscriber] Validating payload for topic: {}", topic);
            // Validate payload
            if (event == null) {
                log.warn("Received empty or null payload from topic: {}, discarding message", topic);
                return; // Discard empty messages
            }

            MessageHandler handler = handlers.get(topic);
            if (handler != null) {
                log.info("[KafkaMessageSubscriber] Found handler for topic: {}: {}", topic, handler.getClass().getSimpleName());
                try {
                    handler.onMessage(topic, event);
                    log.info("[KafkaMessageSubscriber] Successfully processed message from topic: {}", topic);
                } catch (Exception e) {
                    log.error("Error processing message from topic: {}, message will be discarded", topic, e);
                    // Log message details for debugging (without exposing sensitive data)
                    logMessageDetailsForDebugging(topic, messageHeaders, e);
                    // Optionally send to dead letter topic
                   // sendToDeadLetterTopic(topic, payload, messageHeaders, e);
                }
            } else {
                log.warn("No handler found for topic: {}, discarding message", topic);
            }
        } finally {
            log.info("[KafkaMessageSubscriber] Acknowledging message for topic: {}", topic);
            // ALWAYS acknowledge the message to prevent redelivery on restart
            // This is crucial - even failed messages should be acked to avoid infinite reprocessing
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
                log.trace("Message acknowledged for topic: {}", topic);
            }
        }
    }

    /**
     * Check if the message has deserialization errors
     */
    private boolean hasDeserializationError(org.springframework.messaging.MessageHeaders headers) {
        log.info("[KafkaMessageSubscriber] Checking for deserialization error in headers");
        // Check for ErrorHandlingDeserializer error headers using multiple possible header names
        Object deserializationException = headers.get(VALUE_DESERIALIZER_EXCEPTION_HEADER);
        Object keyDeserializationException = headers.get(KEY_DESERIALIZER_EXCEPTION_HEADER);

        // Also check for alternative header names that might be used
        if (deserializationException == null) {
            deserializationException = headers.get("springDeserializerExceptionValue");
        }
        if (keyDeserializationException == null) {
            keyDeserializationException = headers.get("springDeserializerExceptionKey");
        }

        // Check for any header containing "deserializer" and "exception"
        if (deserializationException == null && keyDeserializationException == null) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                String headerName = entry.getKey().toLowerCase();
                if (headerName.contains("deserializer") && headerName.contains("exception")) {
                    log.debug("Found deserialization error header: {}", entry.getKey());
                    return true;
                }
            }
        }

        return deserializationException != null || keyDeserializationException != null;
    }

    /**
     * Handle deserialization errors by logging and optionally sending to dead letter topic
     */
    private void handleDeserializationError(String topic, org.springframework.messaging.MessageHeaders headers) {
        log.info("[KafkaMessageSubscriber] Handling deserialization error for topic: {}", topic);
        Object valueException = headers.get(VALUE_DESERIALIZER_EXCEPTION_HEADER);
        Object keyException = headers.get(KEY_DESERIALIZER_EXCEPTION_HEADER);

        // Try alternative header names if primary ones are not found
        if (valueException == null) {
            valueException = headers.get("springDeserializerExceptionValue");
        }
        if (keyException == null) {
            keyException = headers.get("springDeserializerExceptionKey");
        }

        if (valueException instanceof DeserializationException) {
            DeserializationException deserEx = (DeserializationException) valueException;
            log.error("Failed to deserialize message value from topic: {}. Error: {}. Original data length: {} bytes. Message discarded.",
                     topic, deserEx.getMessage(), deserEx.getData() != null ? deserEx.getData().length : 0);
        } else if (valueException instanceof Exception) {
            Exception ex = (Exception) valueException;
            log.error("Failed to deserialize message value from topic: {}. Error: {}. Message discarded.",
                     topic, ex.getMessage());
        }

        if (keyException instanceof DeserializationException) {
            DeserializationException deserEx = (DeserializationException) keyException;
            log.error("Failed to deserialize message key from topic: {}. Error: {}. Message discarded.",
                     topic, deserEx.getMessage());
        } else if (keyException instanceof Exception) {
            Exception ex = (Exception) keyException;
            log.error("Failed to deserialize message key from topic: {}. Error: {}. Message discarded.",
                     topic, ex.getMessage());
        }

        // If no specific exceptions found, log general deserialization error
        if (valueException == null && keyException == null) {
            log.error("Deserialization error detected for topic: {} but no exception details found in headers. Message discarded.", topic);
        }

        // Increment metrics for monitoring
        logDeserializationMetrics(topic);
    }

    /**
     * Log message details for debugging without exposing sensitive information
     */
    private void logMessageDetailsForDebugging(String topic, org.springframework.messaging.MessageHeaders headers, Exception e) {
        log.info("[KafkaMessageSubscriber] Logging message details for debugging for topic: {}", topic);
        if (log.isDebugEnabled()) {
            log.debug("Message processing failed for topic: {}. Headers: {}. Error: {}",
                     topic,
                     sanitizeHeaders(headers),
                     e.getMessage());
        }
    }

    /**
     * Sanitize headers to remove sensitive information for logging
     */
    private Map<String, Object> sanitizeHeaders(org.springframework.messaging.MessageHeaders headers) {
        log.info("[KafkaMessageSubscriber] Sanitizing headers for logging");
        Map<String, Object> sanitizedHeaders = new HashMap<>();

        // Only include safe headers for debugging
        String[] safeHeaders = {
            KafkaHeaders.RECEIVED_TOPIC,
            KafkaHeaders.RECEIVED_PARTITION,
            KafkaHeaders.OFFSET,
            KafkaHeaders.RECEIVED_TIMESTAMP,
            "eventType",
            "orchName",
            "stepName",
            "action"
        };

        for (String headerName : safeHeaders) {
            Object value = headers.get(headerName);
            if (value != null) {
                sanitizedHeaders.put(headerName, value);
            }
        }

        return sanitizedHeaders;
    }

    /**
     * Send failed messages to a dead letter topic for later analysis
     */
    private void sendToDeadLetterTopic(String originalTopic, byte[] payload,
                                      org.springframework.messaging.MessageHeaders headers, Exception error) {
        log.info("[KafkaMessageSubscriber] Sending message to dead letter topic for original topic: {}", originalTopic);
        try {
            String deadLetterTopic = originalTopic + ".dead-letter";

            // Create enhanced headers with error information
            Map<String, Object> enhancedHeaders = new HashMap<>(headers);
            enhancedHeaders.put("original_topic", originalTopic);
            enhancedHeaders.put("error_message", error.getMessage());
            enhancedHeaders.put("error_class", error.getClass().getName());
            enhancedHeaders.put("failed_at", System.currentTimeMillis());

            log.info("Sending failed message to dead letter topic: {}", deadLetterTopic);

            // Note: You would need to inject KafkaTemplate or MessagePublisher here
            // For now, just log the intent
            log.debug("Dead letter message would be sent to: {} with enhanced headers", deadLetterTopic);

        } catch (Exception dlqException) {
            log.error("Failed to send message to dead letter topic for original topic: {}", originalTopic, dlqException);
        }
    }

    /**
     * Log metrics for monitoring deserialization failures
     */
    private void logDeserializationMetrics(String topic) {
        log.info("[KafkaMessageSubscriber] Logging deserialization metrics for topic: {}", topic);
        // This could integrate with your metrics system (Micrometer, Prometheus, etc.)
        log.info("Deserialization failure recorded for topic: {}", topic);

        // Example: Increment counter for monitoring
        // meterRegistry.counter("kafka.deserialization.failures", "topic", topic).increment();
    }
}
