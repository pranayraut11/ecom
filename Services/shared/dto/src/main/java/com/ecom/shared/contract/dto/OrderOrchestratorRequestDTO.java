package com.ecom.shared.contract.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class OrderOrchestratorRequestDTO {

    private String userId;
    private String sellerId;
    private UUID orderId;
    private BigDecimal amount;
    private PaymentRequest payment;
    private InventoryRequest inventory;

}