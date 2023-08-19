package com.ecom.product.dto;

import com.ecom.product.entity.Price;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PriceDTO extends Price {

    @NotEmpty
    private String productId;
}
