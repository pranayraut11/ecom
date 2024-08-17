//package com.ecom.orchestrator.message.consumer.order;
//
//import com.ecom.orchestrator.service.specification.order.OrchestratorService;
//import com.ecom.shared.contract.dto.OrderOrchestratorRequestDTO;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//@Component
//public class OrderConsumer {
//
//    @Autowired
//    private OrchestratorService orchestratorService;
//
//    @KafkaListener(topics = "order")
//    public void consumerOrderDetails(String message){
//        ObjectMapper objectMapper = new ObjectMapper();
//        orchestratorService.createTransaction(  objectMapper.convertValue(message, OrderOrchestratorRequestDTO.class));
//    }
//}
