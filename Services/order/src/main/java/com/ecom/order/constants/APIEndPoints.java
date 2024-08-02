package com.ecom.order.constants;

public class APIEndPoints {
    private APIEndPoints() {
    }

    public static final String CART_BASE_URL = "cart";
    public static final String CART_PRODUCT_URL = CART_BASE_URL+"/products";
    public static final String PRODUCT_BASE_URL = "products";
    public static final String PRODUCT_FILTER = PRODUCT_BASE_URL+"/filter";
}
