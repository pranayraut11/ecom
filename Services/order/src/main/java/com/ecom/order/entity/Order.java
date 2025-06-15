package com.ecom.order.entity;

import com.ecom.order.model.Product;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@Document
public class Order extends com.ecom.wrapper.database.mongodb.entity.BaseEntity {
    private String orderId; // unique order identifier
    private String userId;
    private String addressId;
    private List<Product> products;
    private BigDecimal totalAmount;
    private String paymentMode;
    private String paymentProvider;
    private String couponCode;
    private OrderStatus status; // use enum instead of String
    private boolean gift;
    private String instructions;
}
