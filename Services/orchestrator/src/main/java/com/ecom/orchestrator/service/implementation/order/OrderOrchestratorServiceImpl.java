package com.ecom.orchestrator.service.implementation.order;

import com.ecom.orchestrator.dto.OrderRequest;
import com.ecom.orchestrator.dto.OrderResponse;
import com.ecom.orchestrator.constants.enums.WorkflowStepStatus;
import com.ecom.orchestrator.service.implementation.order.steps.InventoryStep;
import com.ecom.orchestrator.service.specification.order.OrchestratorService;
import com.ecom.orchestrator.service.specification.order.Workflow;
import com.ecom.orchestrator.service.specification.order.WorkflowStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class OrderOrchestratorServiceImpl implements OrchestratorService {
    @Autowired
    private WebClient inventory;

    @Autowired
    private WebClient payment;

    @Override
    public Mono<OrderResponse> createTransaction(OrderRequest orderRequest) {
        Workflow workflow = this.getWorkflow();
        return Flux.fromStream(() -> workflow.getWorkflowSteps().stream()).flatMap(WorkflowStep::process).
                handle(((aBoolean, synchronousSink) -> {
                    if (aBoolean)
                        synchronousSink.next(true);
                    else
                        synchronousSink.error(new Exception());

                })).then(Mono.fromCallable(() -> new OrderResponse()).onErrorResume(ex -> this.revertOrder(workflow, orderRequest)));
    }

    private Mono<OrderResponse> revertOrder(final Workflow workflow, final OrderRequest requestDTO) {
        return Flux.fromStream(() -> workflow.getWorkflowSteps().stream())
                .filter(wf -> wf.getStatus().equals(WorkflowStepStatus.COMPLETED))
                .flatMap(WorkflowStep::revert)
                .retry(3)
                .then(Mono.just(new OrderResponse()));
    }


    Workflow getWorkflow() {
        WorkflowStep inventoryStep = new InventoryStep(inventory, null);
        WorkflowStep paymentStep = new InventoryStep(payment, null);
        return new OrderWorkflow(List.of(inventoryStep, paymentStep));
    }
}
