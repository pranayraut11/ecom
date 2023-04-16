//package com.ecom.orchestrator.service.implementation.order.steps;
//
//import com.ecom.orchestrator.constants.enums.InventoryStatus;
//import com.ecom.orchestrator.constants.enums.PaymentStatus;
//import com.ecom.orchestrator.dto.InventoryResponse;
//import com.ecom.orchestrator.dto.OrderRequest;
//import com.ecom.orchestrator.constants.enums.WorkflowStepStatus;
//import com.ecom.orchestrator.service.specification.order.WorkflowStep;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.BodyInserters;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//@Service
//public class PaymentStep implements WorkflowStep {
//
//    private WebClient webClient;
//    private WorkflowStepStatus workflowStepStatus = WorkflowStepStatus.INITIATED;
//    private OrderRequest orderRequest;
//
//    public PaymentStep(WebClient webClient, OrderRequest orderRequest) {
//        this.webClient = webClient;
//        this.orderRequest = orderRequest;
//    }
//
//    @Override
//    public WorkflowStepStatus getStatus() {
//        return this.workflowStepStatus;
//    }
//
//    @Override
//    public Mono<Boolean> process() {
//        return this.webClient
//                .post()
//                .uri("/inventory/deduct")
//                .body(BodyInserters.fromValue(this.orderRequest))
//                .retrieve()
//                .bodyToMono(InventoryResponse.class)
//                .map(r -> r.getStatus().equals(PaymentStatus.PAYMENT_APPROVED))
//                .doOnNext(b -> this.workflowStepStatus = b ? WorkflowStepStatus.COMPLETED : WorkflowStepStatus.FAILED);
//
//    }
//
//    @Override
//    public Mono<Boolean> revert() {
//        return this.webClient
//                .post()
//                .uri("/inventory/add")
//                .body(BodyInserters.fromValue(this.orderRequest))
//                .retrieve()
//                .bodyToMono(Void.class)
//                .map(r ->true)
//                .onErrorReturn(false);
//    }
//}
