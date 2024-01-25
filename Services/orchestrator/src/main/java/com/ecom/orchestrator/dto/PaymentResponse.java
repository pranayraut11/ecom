package com.ecom.orchestrator.dto;

import com.ecom.orchestrator.enums.PaymentStatus;
import lombok.Data;

@Data
public class PaymentResponse {

    private PaymentStatus status;

    private String transactionId;

    private String orderId;

    private double amount;

}
