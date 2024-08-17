package com.ecom.shared.contract.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderOrchestratorRequestDTO {

    private String userId;
    private String sellerId;
    private UUID orderId;
    private BigDecimal amount;
    private PaymentRequest payment;
    private InventoryRequest inventory;

}