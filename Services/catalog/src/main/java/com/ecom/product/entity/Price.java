package com.ecom.product.entity;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Price {

	private BigDecimal price;
	private BigDecimal discountedPrice;
	private BigDecimal discount;
}
