package com.ecom.product.constant;


public enum ExceptionCode {

    AUTH_401("AUTH_ERR001");

    private String errorCode;

    ExceptionCode(String errorCode) {
        this.errorCode = errorCode;
    }


}
