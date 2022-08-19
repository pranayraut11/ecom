package com.ecom.order.entity;

import com.ecom.order.model.Address;
import com.ecom.order.model.Product;
import com.ecom.order.model.Seller;
import lombok.Data;
import org.ecom.shared.entity.BaseEntity;

import javax.persistence.Id;
import java.util.List;

@Data
public class Order extends BaseEntity {

    @Id
    private String orderId;

    private String userId;

    private List<Address> addressList;

    private List<Product> products;

    private Seller seller;

}
