package com.ecom.inventory.entity;

import com.ecom.shared.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class ProductInventory extends BaseEntity {

    private String userId;
    private String productId;
    private int quantity;

}
