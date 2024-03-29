package com.ecom.cart.service.specification;

import com.ecom.cart.entity.Cart;
import com.ecom.cart.entity.Product;

public interface CartService  {

    Cart getCart();

    Cart addProductToCart(Product product);

    Cart updateProduct(Product product);

    Cart removeProductFromCart(String productId);
}
