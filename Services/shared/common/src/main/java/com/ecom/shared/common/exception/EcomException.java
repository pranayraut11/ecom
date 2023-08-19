package com.ecom.shared.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EcomException extends RuntimeException {

    private HttpStatus statusCode;

    private String errorCode;

    private String message;

    public EcomException(HttpStatus statusCode, String errorCode) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public EcomException(HttpStatus statusCode, String errorCode, String message) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.message = message;
    }


    public EcomException(Throwable t) {
        super(t);
    }


}
