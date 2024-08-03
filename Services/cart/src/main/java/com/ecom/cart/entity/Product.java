package com.ecom.cart.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document
@Data
@Builder
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
