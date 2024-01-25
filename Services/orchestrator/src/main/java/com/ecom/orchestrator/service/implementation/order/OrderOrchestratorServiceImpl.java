package com.ecom.orchestrator.service.implementation.order;

import com.ecom.orchestrator.enums.WorkflowStepStatus;
import com.ecom.orchestrator.dto.*;
import com.ecom.orchestrator.mappers.InventoryMapper;
import com.ecom.orchestrator.mappers.OrchestratorMapper;
import com.ecom.orchestrator.mappers.PaymentMapper;
import com.ecom.orchestrator.service.implementation.order.steps.InventoryStep;
import com.ecom.orchestrator.service.implementation.order.steps.PaymentStep;
import com.ecom.orchestrator.service.specification.order.OrchestratorService;
import com.ecom.orchestrator.service.specification.order.Workflow;
import com.ecom.orchestrator.service.specification.order.WorkflowStep;
import com.ecom.shared.common.dto.OrderOrchestratorRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class OrderOrchestratorServiceImpl implements OrchestratorService {

    @Autowired
    @Qualifier("inventory")
    private WebClient inventoryClient;

    @Autowired
    @Qualifier("payment")
    private WebClient paymentClient;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private OrchestratorMapper orchestratorMapper;

    private Workflow getOrderWorkflow(OrderOrchestratorRequestDTO requestDTO){
        WorkflowStep paymentStep = new PaymentStep(paymentClient, paymentMapper.toDTO(requestDTO));
        WorkflowStep inventoryStep = new InventoryStep(inventoryClient, inventoryMapper.toDTO( requestDTO));
        return new OrderWorkflow(List.of(paymentStep, inventoryStep));
    }
    @Override
    public  void createTransaction(final OrderOrchestratorRequestDTO requestDTO) {
      Workflow orderWorkflow =  getOrderWorkflow(requestDTO);
      Flux.fromStream(() -> orderWorkflow.getWorkflowSteps().stream()).
                flatMap(WorkflowStep::process).
                handle(((aBoolean, synchronousSink) -> {
                            if (aBoolean)
                                synchronousSink.next(true);
                            else
                                synchronousSink.error(new Exception());
                        })).then(Mono.fromCallable(()->orchestratorMapper.toORCResponseDTO(requestDTO, OrderStatus.ORDER_COMPLETED))).onErrorResume(ex->this.revertOrder(orderWorkflow, requestDTO)).subscribe();

//                        InventoryRequest inventoryRequest = new InventoryRequest();
//        inventoryRequest.setQuantity(2);
//        inventoryRequest.setProductId("abc");
//        inventoryRequest.setUserId("sellerId");
//        PaymentRequest paymentRequest = new PaymentRequest();
//        paymentRequest.setPaymentMode(PaymentMode.UPI);
//        paymentRequest.setPaymentServiceProvider("PHONEPAY");
//        paymentRequest.setOrderId("abc");
//        paymentRequest.setAmount(BigInteger.TEN);
//        inventoryRestCall.removeFromInventory(inventoryRequest).
//                zipWith(paymentRestCall.doPayment(paymentRequest)).map((tuple) -> OrderResponse.builder().orderId(tuple.getT2().getOrderId())
//                        .transactionId(tuple.getT2().getTransactionId()).paymentStatus(tuple.getT2().getStatus()).build()).subscribe(response -> {
//                    if (response.getPaymentStatus().equals(PaymentStatus.PAYMENT_REJECTED)) {
//                        inventoryRestCall.addToInventory(inventoryRequest);
//                    }
//                });
        //Flux.merge(, paymentRestCall.doPayment(paymentRequest)).parallel().
    }

    private Mono<OrchestratorResponseDTO> revertOrder(final Workflow workflow, final OrderOrchestratorRequestDTO requestDTO){
        return Flux.fromStream(() -> workflow.getWorkflowSteps().stream())
                .filter(wf -> wf.getStatus().equals(WorkflowStepStatus.COMPLETED))
                .flatMap(WorkflowStep::revert)
                .retry(3)
                .then(Mono.just(orchestratorMapper.toORCResponseDTO(requestDTO, OrderStatus.ORDER_CANCELLED)));
    }

}
