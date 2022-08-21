package com.ecom.order.service.specification;

import com.ecom.order.dto.OrderDTO;
import com.ecom.order.entity.OrderProducts;
import com.ecom.order.model.Product;

import java.util.List;

public interface OrderService {

    void createOrder(List<OrderProducts> products);

    List<OrderDTO> getOrders();

}
