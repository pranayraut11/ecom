package com.ecom.payment.service.specification;

import com.ecom.orchestrator.client.dto.ExecutionMessage;
import com.ecom.payment.dto.CreatePaymentRequest;
import com.ecom.payment.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse pay(CreatePaymentRequest createPaymentRequest);

    PaymentResponse refund(String transactionId);

    void payByEvent(ExecutionMessage executionMessage);

    void refundByEvent(ExecutionMessage executionMessage);
}
