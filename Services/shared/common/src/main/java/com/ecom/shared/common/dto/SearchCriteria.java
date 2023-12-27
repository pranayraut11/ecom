package com.ecom.shared.common.dto;

import com.ecom.shared.common.enums.Operator;
import lombok.Data;

@Data
public class SearchCriteria {

    private Operator operator;
    private String key;
    private Object value;
}
