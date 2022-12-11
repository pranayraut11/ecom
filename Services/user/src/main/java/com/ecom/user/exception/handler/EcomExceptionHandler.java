package com.ecom.user.exception.handler;

import com.ecom.user.constant.enums.ExceptionCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class EcomExceptionHandler {

    @ExceptionHandler(value = EcomException.class)
    public ResponseEntity handleException(EcomException exception){
       return ResponseEntity.status(exception.getStatusCode()).body(ExceptionCode.getMessageCode(exception.getFunction(),exception.getStatusCode()));
    }
}
