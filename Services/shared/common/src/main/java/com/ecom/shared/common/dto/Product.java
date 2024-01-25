package com.ecom.shared.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Product {

    private String id;
    private int quantity;
}
