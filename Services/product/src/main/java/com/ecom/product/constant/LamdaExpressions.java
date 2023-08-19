package com.ecom.product.constant;

import com.ecom.shared.common.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;

import java.util.function.Supplier;

public class LamdaExpressions {

    public static Supplier<EntityNotFoundException> throwNotFoundException(String message, String... parameters) {
        return () -> new EntityNotFoundException(HttpStatus.NOT_FOUND, ExceptionCode.PRODUCT_ERROR_6002.getCode(), message, parameters);
    }
}
