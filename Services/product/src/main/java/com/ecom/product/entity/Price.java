package com.ecom.product.entity;

import java.math.BigDecimal;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class Price {

	@NotNull
	private BigDecimal price;
	private BigDecimal discountedPrice;
	private BigDecimal discount;
}
