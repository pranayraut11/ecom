package com.ecom.orchestrator.enums;

public enum PaymentMode {

    UPI("UPI"),CREDIT_CARD("CREDIT_CARD"),DEBIT_CARD("DEBIT_CARD"),INTERNET_BANKING("INTERNET_BANKING");

    private String value;
    PaymentMode(String value) {
        this.value = value;
    }
}