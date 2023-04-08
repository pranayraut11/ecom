package com.ecom.inventory.service.specification;

import com.ecom.inventory.dto.InventoryDTO;

public interface InventoryService {

    void add(InventoryDTO inventoryDTO) throws Exception;

    void remove(InventoryDTO inventoryDTO) throws Exception;
}
