package com.ecom.order.service.implementation;

import com.ecom.order.entity.Order;
import com.ecom.order.service.specification.OrderService;
import org.ecom.shared.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class OrderServiceImpl extends BaseService<Order> implements OrderService {
    @Override
    public List<Order> getAll() {
        return Collections.emptyList();
    }

    @Override
    public Order get(String id) {
        return null;
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public Order create(Order entity) {
        return null;
    }

    @Override
    public Order update(Order entity) {
        return null;
    }
}
