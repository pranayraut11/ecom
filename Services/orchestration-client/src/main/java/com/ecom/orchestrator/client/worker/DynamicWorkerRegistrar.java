package com.ecom.orchestrator.client.worker;

import com.ecom.orchestrator.client.config.OrchestrationConfig;
import com.ecom.orchestrator.client.config.OrchestrationLoader;
import com.ecom.orchestrator.client.dto.ExecutionMessage;
import com.ecom.orchestrator.client.service.OrchestrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
@Slf4j
@ConditionalOnProperty(name = "shared.topic", havingValue = "false", matchIfMissing = true)
public class DynamicWorkerRegistrar {

    private final OrchestrationLoader loader;
    private final KafkaListenerContainerFactory<?> kafkaListenerContainerFactory;
    private final KafkaListenerEndpointRegistry registry;
    private final ApplicationContext context;
    private final OrchestrationService orchestrationService;

    // Store handler information by topic
    private final Map<String, HandlerInfo> handlerMap = new ConcurrentHashMap<>();

    public DynamicWorkerRegistrar(OrchestrationLoader loader, KafkaListenerContainerFactory<?> kafkaListenerContainerFactory, KafkaListenerEndpointRegistry registry, ApplicationContext context, OrchestrationService orchestrationService) {
        this.loader = loader;
        this.kafkaListenerContainerFactory = kafkaListenerContainerFactory;
        this.registry = registry;
        this.context = context;
        this.orchestrationService = orchestrationService;
    }


    @EventListener(ApplicationReadyEvent.class)
    public void registerWorkers() {
        loader.getConfig().getOrchestrations().forEach(orc -> {
            if ("worker".equalsIgnoreCase(orc.getAs())) {
                orchestrationService.register(orc);
               log.info(" Detected Worker YAML");
                orc.getSteps().forEach(step -> {
                    // Create DO listener
                    createKafkaListener(orc.getOrchestrationName(), step, false);
                    // Create UNDO listener if undoMethod is present
                    if (step.getUndoMethod() != null && !step.getUndoMethod().isEmpty()) {
                        createKafkaListener(orc.getOrchestrationName(), step, true);
                    }
                });
            }
        });
    }

    private void createKafkaListener(String orchestrationName, OrchestrationConfig.Step step, boolean isUndo) {
        // Create topic name: for UNDO, append ".undo" to the topic
        String baseTopic = "orchestrator." + orchestrationName + "." + step.getName();
        String topic = isUndo ? baseTopic + ".undo" : baseTopic + ".do";

        // Determine which method to use based on isUndo flag
        String methodName;
        if (isUndo) {
            methodName = step.getUndoMethod();
        } else {
            // For DO operation, prefer doMethod over handlerMethod (backward compatibility)
            methodName = step.getDoMethod() != null ? step.getDoMethod() : step.getHandlerMethod();
        }

        if (methodName == null || methodName.isEmpty()) {
            log.warn("⚠️ No method name found for topic: {}, skipping listener registration", topic);
            return;
        }

        MethodKafkaListenerEndpoint<String, String> endpoint = new MethodKafkaListenerEndpoint<>();
        endpoint.setId(String.join("-", orchestrationName, step.getName(), isUndo ? "undo" : "do", "listener"));
        endpoint.setGroupId("dynamic-group");
        endpoint.setTopics(topic);

        try {
            Object bean = context.getBean(step.getHandlerClass());

            // Store handler info in map
            handlerMap.put(topic, new HandlerInfo(bean, methodName));

            // Set up wrapper method
            Method wrapperMethod = this.getClass().getMethod("handleMessage", String.class, String.class);
            endpoint.setBean(this);
            endpoint.setMethod(wrapperMethod);

            // Set MessageHandlerMethodFactory
            try {
                org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory factory =
                        context.getBean(org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory.class);
                endpoint.setMessageHandlerMethodFactory(factory);
            } catch (Exception factoryException) {
                org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory defaultFactory =
                        new org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory();
                defaultFactory.afterPropertiesSet();
                endpoint.setMessageHandlerMethodFactory(defaultFactory);
            }

        } catch (Exception e) {
            log.error("❌ Error configuring handler for topic {} error {}" , topic , e.getMessage());
            return;
        }

        registry.registerListenerContainer(endpoint, kafkaListenerContainerFactory, true);
        log.info("✅ Kafka listener registered for topic: {} → {}.{}()", topic, step.getHandlerClass(), methodName);
    }

    public void handleMessage(@Payload String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        ExecutionMessage executionMessage = null;
        try {
            HandlerInfo handlerInfo = handlerMap.get(topic);
            if (handlerInfo == null) {
               log.error("❌ No handler found for topic: {}", topic);
                return;
            }

            // Deserialize JSON to ExecutionMessage
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            executionMessage = objectMapper.readValue(message, ExecutionMessage.class);

            // Invoke the actual handler method
            Method method = handlerInfo.bean.getClass().getMethod(handlerInfo.methodName, ExecutionMessage.class);
            method.invoke(handlerInfo.bean, executionMessage);

        }
        catch (InvocationTargetException ite) {
            String originalMessage = ite.getTargetException().getMessage();
            log.error("❌ Error in invoked method: {}", originalMessage);
            if (executionMessage != null) {
                executionMessage.getHeaders().put("errorMessage", originalMessage);
                executionMessage.getHeaders().put("status", false);
                orchestrationService.failStep(executionMessage);
            }
        } catch (Exception e) {
           log.error("❌ Error processing message: {}" , e.getMessage());
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
