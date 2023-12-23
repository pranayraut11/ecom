package com.ecom.product.entity;

import com.ecom.product.model.Seller;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

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
