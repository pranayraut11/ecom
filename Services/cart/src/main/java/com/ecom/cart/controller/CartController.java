package com.ecom.cart.controller;

import com.ecom.cart.entity.Cart;
import org.ecom.shared.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("cart")
public class CartController extends BaseController<Cart> {
}
