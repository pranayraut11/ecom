package com.ecom.order.entity;

public enum OrderStatus {
    CREATED,
    CONFIRMED,
    PAYMENT_PENDING,
    PAYMENT_FAILED,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    RETURN_REQUESTED,
    RETURNED,
    REFUNDED
}
