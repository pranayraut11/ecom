package com.ecom.orchestrator.messaging.interfaces;

/**
 * Generic message subscriber interface for broker abstraction
 */
public interface MessageSubscriber {
    void subscribe(String topic, MessageHandler handler);
}
