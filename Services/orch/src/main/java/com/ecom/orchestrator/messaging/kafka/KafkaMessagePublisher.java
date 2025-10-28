package com.ecom.orchestrator.messaging.kafka;

import com.ecom.orchestrator.dto.ExecutionMessage;
import com.ecom.orchestrator.messaging.interfaces.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaMessagePublisher implements MessagePublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void send(String topic, ExecutionMessage message) {
        try {
            // Extract payload as byte array


            // Send the message with headers - Kafka will automatically include Spring Message headers
            kafkaTemplate.send(topic, message);
            log.debug("Message sent to topic: {} with headers: {}", topic, message.getHeaders());
        } catch (Exception e) {
            log.error("Failed to send message to topic: {}", topic, e);
            throw new RuntimeException("Failed to send message", e);
        }
    }
}
