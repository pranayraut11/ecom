package com.ecom.inventory.dto;

import lombok.Data;

@Data
public class InventoryDTO {

    private String productId;
    private String userId;
    private int quantity;

}
