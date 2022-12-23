package com.ecom.product.dto;

import com.ecom.product.entity.Price;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    @Id
    private String id;

    private String name;
    
    private String description;
    
    private Price price;

    private List<MultipartFile> images;

}
