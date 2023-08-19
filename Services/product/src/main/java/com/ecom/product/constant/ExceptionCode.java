package com.ecom.product.constant;


import lombok.Getter;

@Getter
public enum ExceptionCode {

    AUTH_401_01("AUTH_ERR001", "6001"),
    PRODUCT_ERROR_6002("PRODUCT","6002");

    private final String errorCode;
    private String code;

    ExceptionCode(String errorCode, String code) {
        this.errorCode = errorCode;
        this.code = code;
    }

}
