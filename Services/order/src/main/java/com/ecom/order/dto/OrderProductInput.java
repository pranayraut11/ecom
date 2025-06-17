package com.ecom.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderProductInput {
    private String productId;
    private int quantity;
    private String sku; // or variant info
    private String sellerId; // to support multi-seller
    private BigDecimal price; // to support price validation
}
