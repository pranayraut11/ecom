package com.ecom.user.exception.handler;

import com.ecom.user.constant.enums.ExceptionCode;
import com.ecom.user.constant.enums.Function;
import org.ecom.shared.dto.ApiError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class EcomExceptionHandler {

    @ExceptionHandler(value = EcomException.class)
    public ResponseEntity<ApiError> handleException(EcomException exception) {
        return ResponseEntity.status(exception.getStatusCode()).body(new ApiError(exception.getStatusCode(),ExceptionCode.getMessageCode(exception.getFunction(), exception.getStatusCode().value()).messageCode));
    }

    @ExceptionHandler(value = HttpClientErrorException.class)
    public ResponseEntity<ApiError> handleHttpClientErrorException(HttpClientErrorException httpClientErrorException) {
        return ResponseEntity.status(httpClientErrorException.getStatusCode()).body(new ApiError(httpClientErrorException.getStatusCode(),ExceptionCode.getMessageCode(Function.AUTHENTICATION, httpClientErrorException.getStatusCode().value()).messageCode));
    }

}
