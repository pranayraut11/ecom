package com.ecom.cart.service.specification;

import com.ecom.cart.entity.Cart;
import com.ecom.cart.entity.Product;
import org.ecom.shared.service.BaseService;

public interface CartProductService {

    Cart getCart();

    Cart addProductToCart(Product product);

    Cart updateProduct(Product product);

    Cart removeProductFromCart(String productId);

}
