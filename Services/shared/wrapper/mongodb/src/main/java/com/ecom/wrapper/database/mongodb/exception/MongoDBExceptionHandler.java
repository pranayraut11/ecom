package com.ecom.wrapper.database.mongodb.exception;

import com.ecom.shared.contract.dto.ApiError;
import com.ecom.wrapper.database.mongodb.enums.DatabaseExceptionErrorCode;
import com.mongodb.MongoTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MongoDBExceptionHandler {


    @ExceptionHandler(value = MongoTimeoutException.class)
    public ResponseEntity<ApiError> handleMongoTimeoutException(MongoTimeoutException exception) {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(new ApiError(HttpStatus.REQUEST_TIMEOUT, DatabaseExceptionErrorCode.DATABASE_CONNECTION_TIMEOUT.getErrorCode(), exception.getMessage()));
    }


}
