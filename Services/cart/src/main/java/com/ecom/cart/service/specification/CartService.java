package com.ecom.cart.service.specification;

import com.ecom.cart.entity.Cart;
import com.ecom.cart.entity.Product;

import java.util.List;

public interface CartService {

    List<Cart> getAll();

    Cart get(String id);

    void delete(String id);

    Cart create(Cart entity);

    Cart update(Cart entity);

    Cart getCart();

    Cart addProductToCart(Product product);

    Cart updateProduct(Product product);

    Cart removeProductFromCart(String productId);
}
