package com.ecom.orchestrator.config;

import com.ecom.orchestrator.messaging.interfaces.MessageSubscriber;
import com.ecom.orchestrator.service.OrchestrationMessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageListenerConfig implements CommandLineRunner {

    private final MessageSubscriber messageSubscriber;
    private final OrchestrationMessageHandler messageHandler;

    @Override
    public void run(String... args) {
        // Subscribe to orchestration topics
        messageSubscriber.subscribe("orchestrator.registration", messageHandler);
        messageSubscriber.subscribe("orchestrator.execution.start", messageHandler);
        messageSubscriber.subscribe("orchestrator.response.result", messageHandler);

        // Note: Step-specific topics are subscribed to dynamically when orchestrations are registered
    }
}
