package com.ecom.orchestrator.dto;

import com.ecom.orchestrator.constants.enums.PaymentMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.math.BigInteger;

@Data
public class PaymentRequest {
    @NotEmpty
    private PaymentMode paymentMode;
    @NotNull
    private BigInteger amount;
    @NotEmpty
    private String paymentServiceProvider;
    @NotEmpty
    private String orderId;
}