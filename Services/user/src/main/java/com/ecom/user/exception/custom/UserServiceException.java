package com.ecom.user.exception.custom;

public class UserServiceException extends RuntimeException{

    public UserServiceException (String message, Throwable cause) {
        super(message, cause);
    }
}
