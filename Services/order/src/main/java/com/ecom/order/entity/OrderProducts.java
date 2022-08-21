package com.ecom.order.entity;

import com.ecom.order.model.Address;
import com.ecom.order.model.Product;
import com.ecom.order.model.Seller;
import lombok.Builder;
import lombok.Data;
import org.ecom.shared.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@Document
public class OrderProducts extends BaseEntity {

    @Id
    private String orderId;

    private String userId;

    private List<Address> addresses;

    private String productId;

    private String name;

    private String description;

    private Seller seller;

    private String image;

    private BigDecimal price;

    private BigDecimal discountedPrice;

    private short discount;

    private short quantity;
}
