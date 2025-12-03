package com.ecom.orchestrator.client.worker;

import com.ecom.orchestrator.client.config.OrchestrationLoader;
import com.ecom.orchestrator.client.dto.ExecutionMessage;
import com.ecom.orchestrator.client.service.OrchestrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ecom.orchestrator.client.constants.Constant.ORCHESTRATOR_EVENT;

@Component
@Slf4j
@ConditionalOnProperty(name = "shared.topic", havingValue = "true")
public class DynamicEventRouter {
    private final OrchestrationLoader loader;
    private final ApplicationContext context;
    private final OrchestrationService orchestrationService;
    // Map eventType to handler info
    private final Map<String, HandlerInfo> eventTypeHandlerMap = new ConcurrentHashMap<>();

    public DynamicEventRouter(OrchestrationLoader loader, ApplicationContext context, OrchestrationService orchestrationService) {
        this.loader = loader;
        this.context = context;
        this.orchestrationService = orchestrationService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registerEventTypeHandlers() {
        loader.getConfig().getOrchestrations().forEach(orc -> {
            if ("worker".equalsIgnoreCase(orc.getAs())) {
                orc.getSteps().forEach(step -> {
                    // Register DO eventType
                    if (step.getDoMethod() != null && step.getName() != null) {
                        registerHandler("do"+step.getName(), step.getHandlerClass(), step.getDoMethod());
                    }
                    // Register UNDO eventType
                    if (step.getUndoMethod() != null && step.getName() != null) {
                        registerHandler("undo"+step.getName(), step.getHandlerClass(), step.getUndoMethod());
                    }
                });
                orchestrationService.register(orc);
            }
        });
        log.info("DynamicEventRouter: Registered eventType handlers: {}", eventTypeHandlerMap.keySet());
    }

    private void registerHandler(String eventType, String handlerClass, String methodName) {
        try {
            Object bean = context.getBean(handlerClass);
            eventTypeHandlerMap.put(eventType, new HandlerInfo(bean, methodName));
            log.info("Registered handler for eventType: {} -> {}.{}", eventType, handlerClass, methodName);
        } catch (Exception e) {
            log.error("Error registering handler for eventType {}: {}", eventType, e.getMessage());
        }
    }

    // This method should be called by your Kafka listener, passing the raw message
    @KafkaListener(topics = ORCHESTRATOR_EVENT, groupId = "${spring.application.name}")
    public void routeEvent(@Payload String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        ExecutionMessage executionMessage = null;
        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            executionMessage = objectMapper.readValue(message, ExecutionMessage.class);
            executionMessage.getHeaders().forEach((k, v) -> log.info("Header: {} = {}", k, v));
            log.info("Routing event from topic {}: {}", topic, executionMessage.getHeaders());
            String eventType = (String) executionMessage.getHeaders().get("eventType");
            if (eventType == null) {
                log.error("No eventType found in message headers");
                return;
            }
            HandlerInfo handlerInfo = eventTypeHandlerMap.get(eventType);
            if (handlerInfo == null) {
                log.error("No handler registered for eventType: {}", eventType);
                return;
            }
            Method method = handlerInfo.bean.getClass().getMethod(handlerInfo.methodName, ExecutionMessage.class);
            method.invoke(handlerInfo.bean, executionMessage);
        } catch (InvocationTargetException ite) {
            String originalMessage = ite.getTargetException().getMessage();
            log.error("Error in invoked method: {}", originalMessage);
            if (executionMessage != null) {
                executionMessage.getHeaders().put("errorMessage", originalMessage);
                executionMessage.getHeaders().put("status", false);
                orchestrationService.failStep(executionMessage);
            }
        } catch (Exception e) {
            log.error("Error routing event: {}", e.getMessage());
            if (executionMessage != null) {
                executionMessage.getHeaders().put("errorMessage", e.getMessage());
                executionMessage.getHeaders().put("status", false);
                orchestrationService.undoNext(executionMessage);
            }
        }
    }

    private static class HandlerInfo {
        final Object bean;
        final String methodName;
        HandlerInfo(Object bean, String methodName) {
            this.bean = bean;
            this.methodName = methodName;
        }
    }
}

