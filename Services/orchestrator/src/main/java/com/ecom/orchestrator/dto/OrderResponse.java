package com.ecom.orchestrator.dto;

import com.ecom.orchestrator.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {

    private String orderId;
    private String transactionId;
    private String userId;
    private PaymentStatus paymentStatus;
}
