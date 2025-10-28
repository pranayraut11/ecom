package com.ecom.orchestrator.messaging.interfaces;

import com.ecom.orchestrator.dto.ExecutionMessage;
import org.springframework.messaging.Message;

/**
 * Generic message handler interface
 */
public interface MessageHandler {
    void onMessage(String topic, ExecutionMessage message);
}
