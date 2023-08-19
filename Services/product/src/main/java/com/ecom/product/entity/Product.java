package com.ecom.product.entity;

import com.ecom.product.model.Seller;
import com.ecom.shared.common.entity.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Document("product")
@Data
@Getter
@Setter
public class Product extends BaseEntity {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private List<String> images;

    private List<Price> prices;
}
