package com.ecom.orchestrator.service.implementation.order;

import com.ecom.orchestrator.constants.enums.WorkflowStepStatus;
import com.ecom.orchestrator.dto.*;
import com.ecom.orchestrator.rest.InventoryRestCall;
import com.ecom.orchestrator.rest.PaymentRestCall;
import com.ecom.orchestrator.service.implementation.order.steps.InventoryStep;
import com.ecom.orchestrator.service.implementation.order.steps.PaymentStep;
import com.ecom.orchestrator.service.specification.order.OrchestratorService;
import com.ecom.orchestrator.service.specification.order.Workflow;
import com.ecom.orchestrator.service.specification.order.WorkflowStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
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

    private Workflow getOrderWorkflow(OrchestratorRequestDTO requestDTO){
        WorkflowStep paymentStep = new PaymentStep(paymentClient, getPaymentRequestDTO(requestDTO));
        WorkflowStep inventoryStep = new InventoryStep(inventoryClient, this.getInventoryRequestDTO(requestDTO));
        return new OrderWorkflow(List.of(paymentStep, inventoryStep));
    }

    private PaymentRequestDTO getPaymentRequestDTO(OrchestratorRequestDTO requestDTO){
        PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO();
        paymentRequestDTO.setUserId(requestDTO.getUserId());
        paymentRequestDTO.setAmount(requestDTO.getAmount());
        paymentRequestDTO.setOrderId(requestDTO.getOrderId());
        return paymentRequestDTO;
    }


    private InventoryRequestDTO getInventoryRequestDTO(OrchestratorRequestDTO requestDTO){
        InventoryRequestDTO inventoryRequestDTO = new InventoryRequestDTO();
        inventoryRequestDTO.setUserId(requestDTO.getUserId());
        inventoryRequestDTO.setProductId(requestDTO.getProductId());
        inventoryRequestDTO.setOrderId(requestDTO.getOrderId());
        return inventoryRequestDTO;
    }
    @Override
    public  void createTransaction(final OrchestratorRequestDTO requestDTO) {
      Workflow orderWorkflow =  getOrderWorkflow(requestDTO);
      Flux.fromStream(() -> orderWorkflow.getWorkflowSteps().stream()).
                flatMap(WorkflowStep::process).
                handle(((aBoolean, synchronousSink) -> {
                            if (aBoolean)
                                synchronousSink.next(true);
                            else
                                synchronousSink.error(new Exception());
                        })).then(Mono.fromCallable(()->getResponseDTO(requestDTO, OrderStatus.ORDER_COMPLETED))).onErrorResume(ex->this.revertOrder(orderWorkflow, requestDTO)).subscribe();

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

    private Mono<OrchestratorResponseDTO> revertOrder(final Workflow workflow, final OrchestratorRequestDTO requestDTO){
        return Flux.fromStream(() -> workflow.getWorkflowSteps().stream())
                .filter(wf -> wf.getStatus().equals(WorkflowStepStatus.COMPLETED))
                .flatMap(WorkflowStep::revert)
                .retry(3)
                .then(Mono.just(this.getResponseDTO(requestDTO, OrderStatus.ORDER_CANCELLED)));
    }

    private OrchestratorResponseDTO getResponseDTO(OrchestratorRequestDTO requestDTO, OrderStatus status){
        OrchestratorResponseDTO responseDTO = new OrchestratorResponseDTO();
        responseDTO.setOrderId(requestDTO.getOrderId());
        responseDTO.setAmount(requestDTO.getAmount());
        responseDTO.setProductId(requestDTO.getProductId());
        responseDTO.setUserId(requestDTO.getUserId());
        responseDTO.setStatus(status);
        return responseDTO;
    }
}
