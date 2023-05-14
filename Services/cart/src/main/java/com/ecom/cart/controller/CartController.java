package com.ecom.cart.controller;


import com.ecom.cart.service.specification.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("cart")
public class CartController {


    @Autowired
    private CartService cartService;

    @DeleteMapping("{productId}")
    public void removeCart(@PathVariable String productId){
        cartService.delete(productId);
    }


}
