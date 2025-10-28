package com.ecom.orchestrator.messaging.interfaces;

/**
 * Topic management interface for broker abstraction
 */
public interface TopicManager {
    boolean topicExists(String topic);
    void createTopic(String topic);
}
