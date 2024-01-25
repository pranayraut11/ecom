package com.ecom.payment.dto;

import com.ecom.payment.constants.enums.PaymentMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.math.BigInteger;

@Data
public class CreatePaymentRequest {
    @NotEmpty
    private PaymentMode paymentMode;
    @NotNull
    private BigInteger amount;
    @NotEmpty
    private String paymentServiceProvider;
    @NotEmpty
    private String orderId;
}
