package com.ecom.shared.contract.dto;

import com.ecom.shared.contract.enums.Operator;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchCriteria {

    private Operator operator;
    private String key;
    private Object value;
}
