package com.ecom.orchestrator.service.implementation.order.steps;

import com.ecom.orchestrator.constants.enums.InventoryStatus;
import com.ecom.orchestrator.constants.enums.WorkflowStepStatus;
import com.ecom.orchestrator.dto.InventoryRequest;
import com.ecom.orchestrator.dto.InventoryRequestDTO;
import com.ecom.orchestrator.dto.InventoryResponse;
import com.ecom.orchestrator.service.specification.order.WorkflowStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
public class InventoryStep implements WorkflowStep {

    private WebClient webClient;
    private WorkflowStepStatus workflowStepStatus = WorkflowStepStatus.INITIATED;
    private InventoryRequestDTO inventoryRequest;

    public InventoryStep(WebClient webClient, InventoryRequestDTO inventoryRequest) {
        this.webClient = webClient;
        this.inventoryRequest = inventoryRequest;
    }

    @Override
    public WorkflowStepStatus getStatus() {
        return this.workflowStepStatus;
    }

    @Override
    public Mono<Boolean> process() {
        System.out.println("Processing inventory");
        return this.webClient
                .post()
                .uri("/inventory/deduct")
                .body(BodyInserters.fromValue(this.inventoryRequest))
                .retrieve()
                .bodyToMono(InventoryResponse.class)
                .map(r -> r.getStatus().equals(InventoryStatus.AVAILABLE))
                .doOnNext(b -> this.workflowStepStatus = b ? WorkflowStepStatus.COMPLETED : WorkflowStepStatus.FAILED);

    }

    @Override
    public Mono<Boolean> revert() {
        return this.webClient
                .post()
                .uri("/inventory/add")
                .body(BodyInserters.fromValue(this.inventoryRequest))
                .retrieve()
                .bodyToMono(Void.class)
                .map(r ->true)
                .onErrorReturn(false);
    }
}
