package com.ecom.authprovider.exception;

import com.ecom.shared.common.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * Global exception handler for the auth provider service.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles KeycloakServiceException.
     *
     * @param ex the exception
     * @param request the current request
     * @return a ResponseEntity with error details
     */
    @ExceptionHandler(KeycloakServiceException.class)
    public ResponseEntity<ErrorResponse> handleKeycloakServiceException(KeycloakServiceException ex, WebRequest request) {
        log.error("Keycloak service error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse =
        new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Keycloak Operation Failed : " + ex.getMessage(),
                ((ServletWebRequest) request).getRequest().getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
