package com.ecom.product.model;

import com.ecom.product.entity.Price;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class Seller {
    @NotEmpty
    private String id;
    @NotEmpty
    private String name;
    private Float rating;
}
