package com.ecom.product.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Document("product")
@Data
@Getter
@Setter
public class Product {

    @Id
    private String id;

    private String name;
    
    private String description;
    
    private Price price;  

    private List<Media> images;

}
