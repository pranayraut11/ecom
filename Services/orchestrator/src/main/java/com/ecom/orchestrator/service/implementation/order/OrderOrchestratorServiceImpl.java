package com.ecom.orchestrator.service.implementation.order;

import com.ecom.orchestrator.constants.enums.PaymentMode;
import com.ecom.orchestrator.constants.enums.PaymentStatus;
import com.ecom.orchestrator.dto.InventoryRequest;
import com.ecom.orchestrator.dto.OrderRequest;
import com.ecom.orchestrator.dto.OrderResponse;
import com.ecom.orchestrator.dto.PaymentRequest;
import com.ecom.orchestrator.rest.InventoryRestCall;
import com.ecom.orchestrator.rest.PaymentRestCall;
import com.ecom.orchestrator.service.specification.order.OrchestratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@Service
@Slf4j
public class OrderOrchestratorServiceImpl implements OrchestratorService {
    @Autowired
    private InventoryRestCall inventoryRestCall;

    @Autowired
    private PaymentRestCall paymentRestCall;

    @Override
    public void createTransaction(OrderRequest orderRequest) {
        InventoryRequest inventoryRequest = new InventoryRequest();
        inventoryRequest.setQuantity(2);
        inventoryRequest.setProductId("abc");
        inventoryRequest.setUserId("sellerId");
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setPaymentMode(PaymentMode.UPI);
        paymentRequest.setPaymentServiceProvider("PHONEPAY");
        paymentRequest.setOrderId("abc");
        paymentRequest.setAmount(BigInteger.TEN);
        inventoryRestCall.removeFromInventory(inventoryRequest).
                zipWith(paymentRestCall.doPayment(paymentRequest)).map((tuple) -> OrderResponse.builder().orderId(tuple.getT2().getOrderId())
                        .transactionId(tuple.getT2().getTransactionId()).paymentStatus(tuple.getT2().getStatus()).build()).subscribe(response -> {
                    if (response.getPaymentStatus().equals(PaymentStatus.PAYMENT_REJECTED)) {
                        inventoryRestCall.addToInventory(inventoryRequest);
                    }
                });
        //Flux.merge(, paymentRestCall.doPayment(paymentRequest)).parallel().
    }

}
