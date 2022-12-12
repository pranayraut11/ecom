package com.ecom.user.exception.handler;

import com.ecom.user.constant.enums.Function;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EcomException extends RuntimeException{

    private Function function;
    private HttpStatus statusCode;

    public EcomException(Function function,HttpStatus statusCode) {
        this.function = function;
        this.statusCode = statusCode;
    }


}
