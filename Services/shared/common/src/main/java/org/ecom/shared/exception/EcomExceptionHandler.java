package org.ecom.shared.exception;

import org.ecom.shared.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class EcomExceptionHandler {

    @ExceptionHandler(value = EcomException.class)
    public ResponseEntity<ApiError> handleException(EcomException exception) {
        return ResponseEntity.status(exception.getStatusCode()).body(new ApiError(exception.getStatusCode(),exception.getErrorCode(),exception.isConvert()?"Converted message":exception.getMessage()));
    }

    @ExceptionHandler(value = HttpClientErrorException.class)
    public ResponseEntity<ApiError> handleHttpClientErrorException(HttpClientErrorException httpClientErrorException) {
        return ResponseEntity.status(httpClientErrorException.getStatusCode()).body(new ApiError(httpClientErrorException.getStatusCode(),httpClientErrorException.getLocalizedMessage()));
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleException(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(HttpStatus.BAD_REQUEST,exception.getMessage()));
    }


}
