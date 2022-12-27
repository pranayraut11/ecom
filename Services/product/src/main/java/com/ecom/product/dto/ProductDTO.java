package com.ecom.product.dto;

import com.ecom.product.entity.Price;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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