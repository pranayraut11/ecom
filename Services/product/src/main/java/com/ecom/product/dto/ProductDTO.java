package com.ecom.product.dto;

import com.ecom.product.entity.Price;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private String id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    @NotNull
    @Valid
    private Price price;

    private List<String> images;

}
