package com.ecom.order.controller;

import com.ecom.order.dto.OrderDTO;
import com.ecom.order.entity.OrderProducts;
import com.ecom.order.service.specification.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("order")
@CrossOrigin("*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping()
    public void createOrders(@RequestBody List<OrderProducts> products){
         orderService.createOrder(products);
    }

    @GetMapping()
    public List<OrderDTO> getAllOrders(){
        return orderService.getOrders();
    }
}
