package com.ecom.inventory.service.specification;

import com.ecom.inventory.dto.InventoryDTO;
import com.ecom.inventory.enums.InventoryStatus;
import com.ecom.shared.exception.EcomException;

import java.util.List;

public interface InventoryService {

    void add(InventoryDTO inventoryDTO) throws EcomException;

    InventoryStatus remove(InventoryDTO inventoryDTO) throws EcomException;

    List<String> checkStockAvailability(List<InventoryDTO> inventoryDTO) throws EcomException;
}
