package com.ecom.product.entity;

import java.math.BigDecimal;

import com.ecom.product.model.Seller;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class Price {

	@NotNull
	private BigDecimal price;
	private BigDecimal discountedPrice;
	private BigDecimal discount;
	private boolean isInStock;
	@NotNull
	private Seller seller;
}
