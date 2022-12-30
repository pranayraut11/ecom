package com.ecom.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;

@AllArgsConstructor
@Getter
public class BadRequest extends RuntimeException{

    private String message;
    private HashMap<String,String> errors;

}
