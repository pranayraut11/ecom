package com.ecom.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EcomException extends RuntimeException {

    private HttpStatus statusCode;

    private String errorCode;

    private String message;

    private boolean convert;


    public EcomException(HttpStatus statusCode, String errorCode) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public EcomException(HttpStatus statusCode, String errorCode, String message, boolean convert) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.message = message;
        this.convert = convert;
    }


    public EcomException(Throwable t) {
        super(t);
    }


}
