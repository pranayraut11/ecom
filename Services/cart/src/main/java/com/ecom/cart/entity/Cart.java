package com.ecom.cart.entity;

import com.ecom.shared.common.entity.BaseEntity;
import lombok.Builder;
import lombok.Data;
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

    private int discount;

    private BigDecimal total;

}
