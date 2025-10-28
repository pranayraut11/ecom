package com.ecom.orchestrator.client.worker;

import com.ecom.orchestrator.client.config.OrchestrationConfig;
import com.ecom.orchestrator.client.config.OrchestrationLoader;
import com.ecom.orchestrator.client.dto.ExecutionMessage;
import com.ecom.orchestrator.client.service.OrchestrationService;
import lombok.extern.slf4j.Slf4j;
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

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
@Slf4j
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
                orc.getSteps().forEach(step->createKafkaListener(orc.getOrchestrationName(),step));
            }
        });
    }

    private void createKafkaListener(String orchestrationName, OrchestrationConfig.Step step) {
        String topic = "orchestrator." + orchestrationName + "." + step.getName();
        MethodKafkaListenerEndpoint<String, String> endpoint = new MethodKafkaListenerEndpoint<>();
        endpoint.setId(String.join("-",orchestrationName,step.getName(),"listener"));
        endpoint.setGroupId("dynamic-group");
        endpoint.setTopics(topic);

        try {
            Object bean = context.getBean(step.getHandlerClass());

            // Store handler info in map
            handlerMap.put(topic, new HandlerInfo(bean, step.getHandlerMethod()));

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
        log.info("Kafka listener registered for topic: {} {} {}",topic, step.getHandlerClass() ,step.getHandlerMethod());
    }

    public void handleMessage(@Payload String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            HandlerInfo handlerInfo = handlerMap.get(topic);
            if (handlerInfo == null) {
               log.error("❌ No handler found for topic: {}", topic);
                return;
            }

            // Deserialize JSON to ExecutionMessage
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            ExecutionMessage executionMessage = objectMapper.readValue(message, ExecutionMessage.class);

            // Invoke the actual handler method
            Method method = handlerInfo.bean.getClass().getMethod(handlerInfo.methodName, ExecutionMessage.class);
            method.invoke(handlerInfo.bean, executionMessage);

        } catch (Exception e) {
           log.error("❌ Error processing message: {}" , e.getMessage());
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
