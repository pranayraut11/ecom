package com.ecom.order.service.specification;

import com.ecom.order.dto.CreateOrderDTO;
import com.ecom.order.dto.OrderDTO;

import java.util.List;

public interface OrderService {

    void createOrder(CreateOrderDTO createOrderDTO);

    List<OrderDTO> getOrders();

}
