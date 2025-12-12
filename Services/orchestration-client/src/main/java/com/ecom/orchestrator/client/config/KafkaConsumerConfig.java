package com.ecom.orchestrator.client.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.RetryListener;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.util.backoff.FixedBackOff;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaConsumerConfig {

    @Value("${orchestrator.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${orchestrator.kafka.retry.max-attempts:3}")
    private int maxRetryAttempts;

    @Value("${orchestrator.kafka.retry.backoff-interval:2000}")
    private long backoffInterval;

    @Value("${orchestrator.kafka.consumer.auto-offset-reset:latest}")
    private String autoOffsetReset;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        // Configure retry with backoff
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
            new FixedBackOff(backoffInterval, Math.max(0, maxRetryAttempts - 1))
        );

        // Add retry listener for logging
        errorHandler.setRetryListeners(new RetryListener() {
            @Override
            public void failedDelivery(org.apache.kafka.clients.consumer.ConsumerRecord<?, ?> record,
                                      Exception ex, int deliveryAttempt) {
                log.warn("⚠️ Retry attempt {}/{} failed for topic: {} partition: {} offset: {} - Error: {}",
                    deliveryAttempt, maxRetryAttempts, record.topic(), record.partition(),
                    record.offset(), ex.getMessage());
            }

            @Override
            public void recovered(org.apache.kafka.clients.consumer.ConsumerRecord<?, ?> record,
                                 Exception ex) {
                log.info("✅ Message recovered after retry for topic: {} partition: {} offset: {}",
                    record.topic(), record.partition(), record.offset());
            }

            @Override
            public void recoveryFailed(org.apache.kafka.clients.consumer.ConsumerRecord<?, ?> record,
                                      Exception original, Exception failure) {
                log.error("❌ All retry attempts exhausted for topic: {} partition: {} offset: {} - Original error: {}",
                    record.topic(), record.partition(), record.offset(), original.getMessage());
            }
        });

        factory.setCommonErrorHandler(errorHandler);

        // Configure ACK mode
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        return factory;
    }

    @Bean
    public MessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.afterPropertiesSet();
        return factory;
    }
}

