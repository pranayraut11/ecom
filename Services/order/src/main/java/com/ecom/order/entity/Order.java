package com.ecom.order.entity;

import com.ecom.order.model.Address;
import com.ecom.order.model.Product;
import com.ecom.shared.common.entity.BaseEntity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Document
public class Order extends BaseEntity {

    @Id
    private UUID orderId;

    private String userId;

    private List<Address> addresses;

    List<Product> products;
}
