package com.ecom.product.dto;

import com.ecom.product.entity.Price;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
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
    private Price price;

    @NotNull
    private List<MultipartFile> images;

}
