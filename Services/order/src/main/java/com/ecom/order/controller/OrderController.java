package com.ecom.order.controller;

import com.ecom.order.entity.Order;
import org.ecom.shared.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
public class OrderController extends BaseController<Order> {
}
