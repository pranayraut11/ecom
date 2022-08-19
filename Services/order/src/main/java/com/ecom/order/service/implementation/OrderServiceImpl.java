package com.ecom.order.service.implementation;

import com.ecom.order.entity.Order;
import com.ecom.order.repository.OrderRepository;
import com.ecom.order.service.specification.OrderService;
import org.ecom.shared.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class OrderServiceImpl extends BaseService<Order> implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<Order> getAll() {
        List<Order> orders = new ArrayList<>();
        orderRepository.findAll().forEach(orders::add);
        return orders;
    }

    @Override
    public Order get(String id) {
        return orderRepository.findById(id).get();
    }

    @Override
    public void delete(String id) {
        orderRepository.deleteById(id);
    }

    @Override
    public Order create(Order entity) {
        return orderRepository.save(entity);
    }

    @Override
    public Order update(Order entity) {
        return orderRepository.save(entity);
    }
}
