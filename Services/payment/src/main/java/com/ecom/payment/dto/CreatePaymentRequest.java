package com.ecom.payment.dto;

import com.ecom.payment.constants.enums.PaymentMode;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
