package com.ecom.orchestrator.client.service;

import com.ecom.orchestrator.client.config.OrchestrationConfig;
import com.ecom.orchestrator.client.dto.ExecutionMessage;

/**
 * Interface for orchestration services
 */
public interface OrchestrationService {

    /**
     * Starts orchestration with the provided data
     * @param data The data to be used for orchestration (can be YAML content, JSON, etc.)
     * @param topicName Topic name to publish events to
     */
    void startOrchestration(ExecutionMessage data, String orchestratorName, String topicName);

    /**
     * Starts orchestration with the provided data using default topic
     * @param data The data to be used for orchestration (can be YAML content, JSON, etc.)
     */
    void startOrchestration(ExecutionMessage data, String orchestratorName);

    /**
     * Registers orchestration definition without starting it
     * @param data The orchestration definition data (YAML or JSON)
     * @param topicName Topic name to publish events to
     * @return Message object that was sent to Kafka
     */
    void register(OrchestrationConfig.Orchestration data, String topicName);

    /**
     * Registers orchestration definition without starting it using default topic
     * @param data The orchestration definition data (YAML or JSON)
     * @return Message object that was sent to Kafka
     */
    void register(OrchestrationConfig.Orchestration data);


    /**
     * Sends response message to specified topic
     * @param message The execution message containing response data
     * @param topicName The topic name to send the response to
     */
    void sendSuccessResponse(ExecutionMessage message,String topicName);

    /**
     * Sends response message to default topic
     * @param message The execution message containing response data
     */
    void sendSuccessResponse(ExecutionMessage message);


    /**
     * Sends failure response message to specified topic
     * @param message The execution message containing failure data
     * @param topicName The topic name to send the failure response to
     */
    void sendFailureResponse(ExecutionMessage message,String topicName);
    /**
     * Sends failure response message to default topic
     * @param message The execution message containing failure data
     */
    void sendFailureResponse(ExecutionMessage message);
}
