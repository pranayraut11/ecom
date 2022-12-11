package com.ecom.user.exception.handler;

import com.ecom.user.constant.enums.Function;
import lombok.Getter;

@Getter
public class EcomException extends RuntimeException{

    private Function function;
    private int statusCode;

    public EcomException(Function function,int statusCode) {
        this.function = function;
        this.statusCode = statusCode;
    }


}
