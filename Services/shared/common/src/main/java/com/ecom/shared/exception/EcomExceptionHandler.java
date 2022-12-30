package com.ecom.shared.exception;

import com.ecom.shared.config.i18.Translator;
import com.ecom.shared.dto.ApiError;
import com.ecom.shared.dto.ApiSubError;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class EcomExceptionHandler {

    @Autowired
    private Translator translator;

    @ExceptionHandler(value = EcomException.class)
    public ResponseEntity<ApiError> handleException(EcomException exception) {
        return ResponseEntity.status(exception.getStatusCode()).body(new ApiError(exception.getStatusCode(), exception.getErrorCode(), exception.isConvert() ? "Converted message" : exception.getMessage()));
    }

    @ExceptionHandler(value = HttpClientErrorException.class)
    public ResponseEntity<ApiError> handleHttpClientErrorException(HttpClientErrorException httpClientErrorException) {
        return ResponseEntity.status(httpClientErrorException.getStatusCode()).body(new ApiError(httpClientErrorException.getStatusCode(), httpClientErrorException.getLocalizedMessage()));
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException exception) {
        String convertedMessage = translator.translate(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(HttpStatus.BAD_REQUEST, convertedMessage));
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

}
