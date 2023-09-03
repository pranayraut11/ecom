package com.ecom.filemanager.exception;

public class ConnectionFailedException extends RuntimeException{

    public ConnectionFailedException(String message) {
        super(message);
    }
}
