package com.ecom.shared.contract.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderOrchestratorRequestDTO {

    private String userId;
    private String sellerId; // for backward compatibility
    private List<String> sellerIds; // for multi-seller support
    private Map<String, List<Product>> sellerProducts; // sellerId -> products
    private UUID orderId;
    private BigDecimal amount;
    private PaymentRequest payment;
    private InventoryRequest inventory;

    // New fields for richer orchestration
    private String addressId; // or AddressDTO address;
    private LocalDateTime orderTimestamp;
    private String couponCode;
    private String orderStatus;

}