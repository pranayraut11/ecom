package com.ecom.orchestrator.message.consumer.order;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    @KafkaListener(topics = "order")
    public void consumerOrderDetails(String message){

    }
}
