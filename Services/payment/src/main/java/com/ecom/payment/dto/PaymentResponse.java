package com.ecom.payment.dto;

import com.ecom.payment.constants.enums.PaymentStatus;
import lombok.Data;

@Data
public class PaymentResponse {

    private PaymentStatus status;

    private String transactionId;

    private String orderId;

    private double amount;

}