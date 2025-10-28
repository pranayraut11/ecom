package com.ecom.orchestrator.exception;

public class OrchestrationListException extends RuntimeException {

    public OrchestrationListException(String message) {
        super(message);
    }

    public OrchestrationListException(String message, Throwable cause) {
        super(message, cause);
    }
}
