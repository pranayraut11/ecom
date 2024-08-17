package com.ecom.order.entity;

import com.ecom.order.model.Address;
import com.ecom.order.model.Product;
import com.ecom.wrapper.database.mongodb.entity.BaseEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document
public class Order extends BaseEntity {

    private String userId;

    private List<Address> addresses;

    List<Product> products;
}
