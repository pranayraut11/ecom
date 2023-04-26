package com.ecom.inventory.dto;

import com.ecom.inventory.enums.InventoryStatus;
import lombok.Data;

@Data
public class InventoryResponse {

    private InventoryStatus status;
    private String productId;

}
