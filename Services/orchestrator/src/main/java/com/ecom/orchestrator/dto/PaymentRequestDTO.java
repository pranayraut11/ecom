package com.ecom.orchestrator.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PaymentRequestDTO {
    private Integer userId;
    private UUID orderId;
    private BigDecimal amount;
}