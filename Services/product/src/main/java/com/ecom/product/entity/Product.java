package com.ecom.product.entity;

import com.ecom.shared.common.entity.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("product")
@Data
@Getter
@Setter
public class Product extends BaseEntity {

    private String name;
    
    private String description;
    
    private Price price;  

    private List<String> images;



}
