package com.ecom.tenantmanagement.exception;

/**
 * Exception thrown when schema creation fails
 */
public class SchemaCreationException extends RuntimeException {

    public SchemaCreationException(String message) {
        super(message);
    }

    public SchemaCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
