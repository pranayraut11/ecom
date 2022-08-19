package com.ecom.cart.entity;

import lombok.Builder;
import lombok.Data;
import org.ecom.shared.entity.BaseEntity;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.math.BigDecimal;
import java.util.List;

@RedisHash("Cart")
@Data
@Builder
public class Cart extends BaseEntity {

    List<Product> products;

    @Indexed
    private String userId;

    private BigDecimal totalPrice;

    private BigDecimal discount;

    private BigDecimal total;

}
