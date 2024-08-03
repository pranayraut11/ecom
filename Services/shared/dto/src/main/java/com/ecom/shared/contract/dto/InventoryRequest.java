package com.ecom.shared.contract.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InventoryRequest {
    private List<Product> products;
    private String userId;
}
