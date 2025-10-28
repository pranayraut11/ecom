package com.ecom.orchestrator.messaging.interfaces;

import com.ecom.orchestrator.dto.ExecutionMessage;
import org.springframework.messaging.Message;

/**
 * Generic message publisher interface for broker abstraction
 */
public interface MessagePublisher {
    void send(String topic, ExecutionMessage message);
}
