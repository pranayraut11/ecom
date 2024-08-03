package com.ecom.shared.contract.dto;

import com.ecom.shared.contract.enums.PaymentMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PaymentRequest {
    @NotEmpty
    private PaymentMode paymentMode;
    @NotNull
    private BigDecimal amount;
    @NotEmpty
    private String paymentServiceProvider;
    @NotEmpty
    private UUID orderId;
}