package com.ecom.orchestrator.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateOrderDTO {

    private boolean buyNow;

    private String id;
    
    private int quantity;
}
