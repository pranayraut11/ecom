package com.ecom.orchestrator.dto;

import com.ecom.orchestrator.constants.enums.PaymentMode;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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