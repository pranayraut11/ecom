package com.ecom.product.dto;

import lombok.Data;

@Data
public class SearchCriteria {

    private Operator operator;
    private String key;
    private Object value;
}
