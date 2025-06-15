package com.ecom.order.dto;

import lombok.Data;

@Data
public class OrderProductInput {
    private String productId;
    private int quantity;
    private String sku; // or variant info
    private String sellerId; // to support multi-seller
}
