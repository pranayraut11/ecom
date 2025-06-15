package com.ecom.order.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Product {
    private String productId;

    private String name;

    private String description;

    private String image;

    private String sellerId;

    private String sellerName;

    private String sku; // or variant info

    private short quantity;

    private BigDecimal unitPrice;

    private BigDecimal discountedPrice;

    private BigDecimal totalPrice;
}
