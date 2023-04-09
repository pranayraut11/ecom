package com.ecom.payment.constants.enums;

public enum PaymentStatus {

    INITIATED("INITIATED"),WAITING_FOR_RESPONSE("WAITING_FOR_RESPONSE"),FAILED("FAILED"),COMPLETED("COMPLETED");

    private String value;
    PaymentStatus(String value) {
        this.value = value;
    }
}
