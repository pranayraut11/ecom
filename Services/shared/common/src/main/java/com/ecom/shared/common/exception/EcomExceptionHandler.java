package com.ecom.shared.common.exception;

import com.mongodb.MongoWriteException;
import jakarta.ws.rs.NotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.MongoTimeoutException;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
public class EcomExceptionHandler {

    @ExceptionHandler(value = EcomException.class)
    public ResponseEntity<ErrorResponse> handleException(EcomException exception, WebRequest request) {
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(new ErrorResponse(
                        exception.getStatusCode(),
                        exception.getMessage(),
                        ((ServletWebRequest) request).getRequest().getRequestURI()));
    }

    @ExceptionHandler(value = HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientErrorException(
            HttpClientErrorException exception,
            WebRequest request) {

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(new ErrorResponse(
                        HttpStatus.valueOf(exception.getStatusCode().value()),
                        exception.getLocalizedMessage(),
                        ((ServletWebRequest) request).getRequest().getRequestURI()));
    }

    @ExceptionHandler(value = MongoTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleMongoTimeoutException(
            MongoTimeoutException exception,
            WebRequest request) {

        return ResponseEntity
                .status(HttpStatus.REQUEST_TIMEOUT)
                .body(new ErrorResponse(
                        HttpStatus.REQUEST_TIMEOUT,
                        exception.getMessage(),
                        ((ServletWebRequest) request).getRequest().getRequestURI()));
    }

    @ExceptionHandler(value = MongoWriteException.class)
    public ResponseEntity<ErrorResponse> handleMongoWriteException(
            MongoWriteException exception,
            WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = exception.getMessage();

        // Check for duplicate key error
        if (exception.getError().getCode() == 11000) {
            message = "A duplicate record already exists: " + message;
        }

        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(
                        status,
                        message,
                        ((ServletWebRequest) request).getRequest().getRequestURI()));
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception,
            WebRequest request) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        exception.getMessage(),
                        ((ServletWebRequest) request).getRequest().getRequestURI()));
    }

    @ExceptionHandler(value = JsonProcessingException.class)
    public ResponseEntity<ErrorResponse> handleJsonProcessingException(
            JsonProcessingException exception,
            WebRequest request) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        exception.getMessage(),
                        ((ServletWebRequest) request).getRequest().getRequestURI()));
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            EntityNotFoundException exception,
            WebRequest request) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        HttpStatus.NOT_FOUND,
                        exception.getMessage(),
                        ((ServletWebRequest) request).getRequest().getRequestURI()));
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleJakartaNotFoundException(
            jakarta.ws.rs.NotFoundException exception,
            WebRequest request) {

        String requestURI = ((ServletWebRequest) request).getRequest().getRequestURI();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        HttpStatus.NOT_FOUND,
                        "The requested resource could not be found",
                        requestURI));
    }

    @ExceptionHandler(value = IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException exception,
            WebRequest request) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        HttpStatus.CONFLICT,
                        exception.getMessage(),
                        ((ServletWebRequest) request).getRequest().getRequestURI()));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception exception,
            WebRequest request) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred",
                        ((ServletWebRequest) request).getRequest().getRequestURI()));
    }

    @ExceptionHandler({DuplicateKeyException.class})
    public ResponseEntity<ErrorResponse> handleDuplicateKeyException(DuplicateKeyException e, WebRequest request) {
        String uri = request.getContextPath();
        String duplicateField = "";
        String message = e.getMostSpecificCause().getMessage();
        List<ErrorResponse.ValidationError> fieldErrors = new ArrayList<>();
        Pattern pattern = Pattern.compile("dup key: \\{ (.+?): \\\"(.+?)\\\" \\}");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String key = matcher.group(1);   // "email"
            String value = matcher.group(2); // "test@example.com"
            ErrorResponse.ValidationError validationError = new ErrorResponse.ValidationError(key, value);
            fieldErrors.add(validationError);
        }
        ErrorResponse errorResponse =  new ErrorResponse(
                HttpStatus.CONFLICT,
                "A record already exists: " +duplicateField,
                uri
        );
        errorResponse.setFieldErrors(fieldErrors);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                errorResponse
        );
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleInvalidDataException(MethodArgumentNotValidException e,WebRequest webRequest){
        ErrorResponse errorResponse = new  ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid data provided "+e.getBindingResult().getFieldError().getField(),
                ((ServletWebRequest) webRequest).getRequest().getRequestURI());
        errorResponse.addValidationError(e.getParameter().getParameterName(),e.getObjectName());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        "Invalid data provided "+e.getParameter().getParameterName(),
                        ((ServletWebRequest) webRequest).getRequest().getRequestURI()));
    }
}
