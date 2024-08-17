package com.ecom.shared.contract.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Product {
    private String id;
    private int quantity;
    private boolean available;
}
