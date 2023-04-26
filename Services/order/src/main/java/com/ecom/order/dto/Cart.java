package com.ecom.order.dto;

import com.ecom.order.model.Product;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Cart {

    private String id;
    List<Product> products;

    private String userId;

    private BigDecimal totalPrice;

    private int discount;

    private BigDecimal total;

}