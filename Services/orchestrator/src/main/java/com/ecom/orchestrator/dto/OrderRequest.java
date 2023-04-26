package com.ecom.orchestrator.dto;

import lombok.Data;

@Data
public class OrderRequest {

    private String orderId;
    private String userId;
    private int quantity;
}
