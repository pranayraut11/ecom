package com.ecom.cart.controller;


import com.ecom.cart.entity.Cart;
import com.ecom.cart.entity.Product;
import com.ecom.cart.service.specification.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cart")
public class CartController {


    @Autowired
    private CartService cartService;

    @GetMapping()
    public Cart getCart(){
        return cartService.getCart();
    }

    @PostMapping()
    public Cart addProductToCart(@RequestBody Product product){
        return   cartService.addProductToCart(product);
    }

    @PatchMapping()
    public Cart updateProductQuantity(@RequestBody Product product){
        return cartService.updateProduct(product);
    }

    @DeleteMapping()
    public Cart removeProductFromCart(@PathVariable String productId){
        return cartService.removeProductFromCart(productId);
    }

}
