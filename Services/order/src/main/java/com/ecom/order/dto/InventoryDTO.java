package com.ecom.order.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryDTO {

    private String productId;
    private String userId;
    private int quantity;

}
