package com.ecom.orchestrator.dto;

import lombok.Data;

@Data
public class InventoryRequest {
    private String productId;
    private String userId;
    private int quantity;
}
