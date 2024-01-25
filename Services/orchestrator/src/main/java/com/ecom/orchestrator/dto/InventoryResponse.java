package com.ecom.orchestrator.dto;

import com.ecom.orchestrator.enums.InventoryStatus;
import lombok.Data;

@Data
public class InventoryResponse {
    private InventoryStatus status;
}
