package com.ecom.user.constant.enums;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ExceptionCode {

    AUTH_401(Function.AUTHENTICATION,401,"Invalid credentials");
    private Function function;
    private int statusCode;
    public String messageCode;

    ExceptionCode(Function function, int statusCode, String messageCode) {
        this.function = function;
        this.statusCode = statusCode;
        this.messageCode = messageCode;
    }

    public static ExceptionCode getMessageCode(Function function,int statusCode){
        return Stream.of(values()).filter(fun->fun.function.equals(function) && fun.statusCode == statusCode).collect(Collectors.toList()).stream().findFirst().get();
    }

}
