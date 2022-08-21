package com.ecom.cart.utility;

import com.ecom.cart.entity.Cart;

import java.math.BigDecimal;
import java.util.Objects;

public  class PriceCalculationUtil {

    public static Cart getCartPrice(Cart cart){
        if (Objects.nonNull(cart)) {
            BigDecimal totalPrice = cart.getProducts().stream().map(product -> product.getPrice().multiply(BigDecimal.valueOf(product.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal total = cart.getProducts().stream().map(product -> product.getDiscountedPrice().multiply(BigDecimal.valueOf(product.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
            cart.setTotalPrice(totalPrice);
            cart.setDiscount(totalPrice.subtract(total).intValue());
            cart.setTotal(total);
        }
        return cart;
    }
}
