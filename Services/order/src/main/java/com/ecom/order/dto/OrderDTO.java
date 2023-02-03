package com.ecom.order.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class OrderDTO {

    private String orderId;

    private String productId;
    private String productName ;
    private String image;
    private BigDecimal price;
    private LocalDate deliveryDate;
    private String deliveryStatus;

}
