package com.ecom.order.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Product {
    private String productId;

    private String name;

    private String description;

    private Seller seller;

    private String image;

    private BigDecimal price;

    private BigDecimal discountedPrice;

    private short discount;

    private short quantity;
}
