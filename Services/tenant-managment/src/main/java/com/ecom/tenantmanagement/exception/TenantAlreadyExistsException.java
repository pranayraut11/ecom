package com.ecom.tenantmanagement.exception;

/**
 * Exception thrown when a tenant already exists (duplicate)
 */
public class TenantAlreadyExistsException extends RuntimeException {

    public TenantAlreadyExistsException(String message) {
        super(message);
    }

    public TenantAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
