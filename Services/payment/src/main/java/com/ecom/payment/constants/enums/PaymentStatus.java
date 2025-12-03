package com.ecom.payment.constants.enums;

public enum PaymentStatus {

    PAYMENT_REJECTED("PAYMENT_REJECTED"),INITIATED("INITIATED"),WAITING_FOR_RESPONSE_FROM_BANK("WAITING_FOR_RESPONSE_FROM_BANK"),FAILED("FAILED"),PAYMENT_APPROVED("PAYMENT_APPROVED"),REFUNDED("REFUNDED"),PAYMENT_REFUNDED("PAYMENT_REFUNDED");

    private String value;
    PaymentStatus(String value) {
        this.value = value;
    }
}
