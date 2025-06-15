package com.ecom.order.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderDTO {
    // List of products to order (productId, quantity, sku/variant, sellerId)
    private List<OrderProductInput> products;
    // Selected shipping address (addressId only)
    private String addressId;
    // Payment method and details
    private String paymentMode; // e.g., COD, CARD, UPI
    private String paymentProvider; // e.g., Razorpay, Paytm
    // Coupon code (optional)
    private String couponCode;
    // Special instructions (optional)
    private String instructions;
    // Gift option (optional)
    private boolean gift;
    // Buy now or from cart
    private boolean buyNow;
}

