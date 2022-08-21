package com.ecom.order.service.implementation;

import com.ecom.order.dto.OrderDTO;
import com.ecom.order.entity.OrderProducts;
import com.ecom.order.repository.OrderRepository;
import com.ecom.order.service.specification.OrderService;
import org.ecom.shared.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl extends BaseService<OrderProducts> implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<OrderProducts> getAll() {
        List<OrderProducts> orders = new ArrayList<>();
        orderRepository.findAll().forEach(orders::add);
        return orders;
    }

    @Override
    public OrderProducts get(String id) {
        return orderRepository.findById(id).get();
    }

    @Override
    public void delete(String id) {
        orderRepository.deleteById(id);
    }

    @Override
    public OrderProducts create(OrderProducts entity) {
        return orderRepository.save(entity);
    }

    @Override
    public OrderProducts update(OrderProducts entity) {
        return orderRepository.save(entity);
    }

    @Override
    public void createOrder(List<OrderProducts> products) {
        String orderID = UUID.randomUUID().toString();
        products.forEach(product -> {
            product.setOrderId(orderID);
            product.setUserId("pranay");
        });
        orderRepository.saveAll(products);
    }

    @Override
    public List<OrderDTO> getOrders() {
        List<OrderProducts> orderProducts = orderRepository.findByUserId("pranay");
        List<OrderDTO> orderDTOS = new ArrayList<>(orderProducts.size());
        orderProducts.forEach(product -> {
            orderDTOS.add(OrderDTO.builder().productName(product.getName()).image(product.getImage()).deliveryDate(LocalDate.now()).price(product.getDiscountedPrice()).build());
        });
        return orderDTOS;
    }
}
