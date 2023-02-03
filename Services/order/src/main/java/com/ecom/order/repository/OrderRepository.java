package com.ecom.order.repository;

import com.ecom.order.entity.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Order, String> {

    List<Order> findByUserId(String userId);
}
