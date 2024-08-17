package com.ecom.cart.entity;

import com.ecom.wrapper.database.mongodb.entity.BaseEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Document
@Data
public class Cart extends BaseEntity {

    List<Product> products;

    @Indexed
    private String userId;

    private BigDecimal totalPrice;

    private int discount;

    private BigDecimal total;

}
