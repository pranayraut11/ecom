package com.ecom.orchestrator.client.publisher;

import com.ecom.orchestrator.client.dto.ExecutionMessage;

/**
 * Platform-agnostic event publisher interface
 * Implementations can be for Kafka, RabbitMQ, Spring Events, etc.
 */
public interface EventPublisher {

    /**
     * Publishes an event to the specified topic
     * @param event The orchestration event to publish
     */
    void publishEvent(ExecutionMessage event);

    /**
     * Checks if the connection to the messaging platform is available
     * @return true if connection is healthy, false otherwise
     */
    boolean isConnectionHealthy();

    /**
     * Returns the type of this publisher (e.g., "KAFKA", "RABBITMQ", "SPRING_EVENT")
     * @return publisher type
     */
    String getPublisherType();
}
