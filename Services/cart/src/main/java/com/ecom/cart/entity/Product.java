package com.ecom.cart.entity;

import lombok.Data;
import org.springframework.data.redis.core.index.Indexed;

import java.math.BigDecimal;

@Data
public class Product {
    @Indexed
    private String productId;

    private String name;

    private String description;

    private String seller;

    private String image;

    private BigDecimal price;

    private BigDecimal discountedPrice;

    private short discount;

    private short quantity;
}
