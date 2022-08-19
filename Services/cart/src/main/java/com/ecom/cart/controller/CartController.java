package com.ecom.cart.controller;

import com.ecom.cart.entity.Cart;
import com.ecom.cart.entity.Product;
import com.ecom.cart.service.specification.CartService;
import org.ecom.shared.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cart")
@CrossOrigin("*")
public class CartController extends BaseController<Cart> {

    @Autowired
    private CartService cartService;

    @GetMapping("products")
    public Cart getCart(){
        return cartService.getCart();
    }

    @PostMapping("products")
    public Cart addProductToCart(@RequestBody Product product){
      return   cartService.addProductToCart(product);
    }

}
