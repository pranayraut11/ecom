package com.ecom.shared.common.exception;

import com.ecom.shared.common.enums.DatabaseExceptionErrorCode;
import com.ecom.shared.contract.dto.ApiError;
import com.ecom.shared.contract.dto.ApiSubError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.MongoTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class EcomExceptionHandler {

    @ExceptionHandler(value = EcomException.class)
    public ResponseEntity<ApiError> handleException(EcomException exception) {
        return ResponseEntity.status(exception.getStatusCode()).body(new ApiError(exception.getStatusCode(), exception.getErrorCode(), exception.getMessage()));
    }

    @ExceptionHandler(value = HttpClientErrorException.class)
    public ResponseEntity<ApiError> handleHttpClientErrorException(HttpClientErrorException httpClientErrorException) {
        return ResponseEntity.status(httpClientErrorException.getStatusCode()).body(new ApiError(httpClientErrorException.getStatusCode(), httpClientErrorException.getLocalizedMessage()));
    }

    @ExceptionHandler(value = MongoTimeoutException.class)
    public ResponseEntity<ApiError> handleMongoTimeoutException(MongoTimeoutException exception) {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(new ApiError(HttpStatus.REQUEST_TIMEOUT, DatabaseExceptionErrorCode.DATABASE_CONNECTION_TIMEOUT.getErrorCode(), exception.getMessage()));
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(HttpStatus.BAD_REQUEST, "convertedMessage"));
    }

    @ExceptionHandler(value = JsonProcessingException.class)
    public ResponseEntity<ApiError> handleJsonProcessingException(JsonProcessingException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }

    @ExceptionHandler(value = BadRequest.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(BadRequest exception) {
        List<ApiSubError> subErrors = exception.getErrors().entrySet().stream().map((k) -> new ApiSubError(k.getKey(), k.getValue())).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(HttpStatus.BAD_REQUEST, exception.getMessage(),subErrors));
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFoundException(EntityNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(HttpStatus.NOT_FOUND, exception.getErrorCode(), exception.getMessage()));
    }

}
