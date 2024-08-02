package com.ecom.shared.common.dto;

import com.ecom.shared.common.enums.Operator;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchCriteria {

    private Operator operator;
    private String key;
    private Object value;
}
