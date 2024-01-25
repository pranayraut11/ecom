package com.ecom.shared.common.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InventoryRequest {
    private List<Product> products;
    private String userId;
}
