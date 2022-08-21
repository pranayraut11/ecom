package com.ecom.order.repository;

import com.ecom.order.entity.OrderProducts;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<OrderProducts, String> {

    List<OrderProducts> findByUserId(String userId);
}
