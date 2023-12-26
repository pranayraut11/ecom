package com.ecom.product.dto;

import lombok.Getter;

@Getter
public enum Operator {
    EQUAL("EQUAL"), NOT_EQUAL("NOT_EQUAL"),GREATER_THAN("GREATER_THAN"),GREATER_THAN_OR_EQUAL_TO("GREATER_THAN_OR_EQUAL_TO"),
    LESS_THAN("LESS_THAN"),LESSTHAN_OR_EQUAL_TO("LESS_THAN"),CONTAINS("CONTAINS"),JOIN("JOIN");

    private final String value;

    Operator(String value) {
        this.value = value;
    }

}
