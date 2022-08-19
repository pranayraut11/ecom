package com.ecom.order.model;

import lombok.Data;

@Data
public class Price {

    private int price;
    private int discountedPrice;
    private short discount;
}
