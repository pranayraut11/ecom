package com.ecom.order.controller;

import com.ecom.order.dto.CreateOrderDTO;
import com.ecom.order.dto.OrderDTO;
import com.ecom.order.service.specification.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("order")
@CrossOrigin("*")
@AllArgsConstructor
public class OrderController {

    private OrderService orderService;

    @PostMapping()
    public void createOrders(@RequestBody CreateOrderDTO createOrder){
         orderService.createOrder(createOrder);
    }

    @GetMapping()
    public List<OrderDTO> getAllOrders(){
        return orderService.getOrders();
    }
}
