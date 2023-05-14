package com.ecom.order.entity;

import com.ecom.order.model.Address;
import com.ecom.order.model.Product;
import com.ecom.shared.common.entity.BaseEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;

@Data
@Builder
@Document
public class Order extends BaseEntity {

    @Id
    private String orderId;

    private String userId;

    private List<Address> addresses;

    List<Product> products;
}
