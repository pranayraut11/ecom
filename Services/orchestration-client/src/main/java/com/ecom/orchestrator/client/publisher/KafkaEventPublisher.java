package com.ecom.orchestrator.client.publisher;

import com.ecom.orchestrator.client.dto.ExecutionMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka implementation - primary event publisher for orchestration events
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher implements EventPublisher {

    @Value("${orchestrator.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Qualifier("messageKafkaTemplate")
    private final KafkaTemplate<String, ExecutionMessage> kafkaTemplate;

    @Override
    public void publishEvent(ExecutionMessage message) {
        try {
            // Extract metadata from message headers
            String orchestrationName = (String) message.getHeaders().get("orchestrationName");
            String topic = (String) message.getHeaders().get("topic");
            String flowId = (String) message.getHeaders().get("flowId");

            log.info("Publishing Kafka event for orchestration: {} to topic: {}",
                    orchestrationName, topic);

            // Send complete Message object to Kafka (headers and payload preserved)
            CompletableFuture<SendResult<String, ExecutionMessage>> future = kafkaTemplate.send(
                topic,
                flowId,
                message
            );

            // Handle success/failure callbacks
            future.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    log.error("Failed to send Kafka message: {} to topic: {}",
                            flowId, topic, throwable);
                    throw new RuntimeException("Failed to publish message to Kafka", throwable);
                } else {
                    log.info("Successfully published Kafka message: {} to topic: {} at offset: {} with headers: {}",
                            flowId,
                            topic,
                            result.getRecordMetadata().offset(),
                            message.getHeaders());
                }
            });

            log.info("Kafka message queued for publishing: {} with {} headers",
                    flowId, message.getHeaders().size());

        } catch (Exception e) {
            String flowId = (String) message.getHeaders().get("flowId");
            log.error("Failed to publish Kafka message: {}", flowId, e);
            throw new RuntimeException("Failed to publish message to Kafka", e);
        }
    }

    @Override
    public boolean isConnectionHealthy() {
        try {
            log.debug("Checking Kafka connection to: {}", bootstrapServers);

            // Try to get cluster metadata to check connection
            var clusterResource = kafkaTemplate.getProducerFactory()
                    .createProducer()
                    .partitionsFor("__consumer_offsets"); // Use internal topic for health check

            log.debug("Kafka connection healthy - bootstrap servers: {}", bootstrapServers);
            return true;

        } catch (Exception e) {
            log.warn("Kafka connection unhealthy - bootstrap servers: {} - error: {}",
                    bootstrapServers, e.getMessage());
            return false;
        }
    }

    @Override
    public String getPublisherType() {
        return "";
    }
}
