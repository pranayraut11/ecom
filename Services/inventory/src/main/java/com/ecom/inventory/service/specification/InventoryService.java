package com.ecom.inventory.service.specification;

import com.ecom.inventory.dto.InventoryDTO;
import com.ecom.inventory.entity.ProductInventory;
import com.ecom.inventory.enums.InventoryStatus;
import com.ecom.orchestrator.client.dto.ExecutionMessage;
import com.ecom.shared.common.exception.EcomException;

import java.util.List;

public interface InventoryService {

    List<ProductInventory> getAll();

    ProductInventory get(String id);

    void delete(String id);

    ProductInventory create(ProductInventory entity);

    ProductInventory update(ProductInventory entity);

    void add(InventoryDTO inventoryDTO) throws EcomException;

    InventoryStatus remove(InventoryDTO inventoryDTO) throws EcomException;

    List<String> checkStockAvailability(List<InventoryDTO> inventoryDTO) throws EcomException;

    void deductStock(List<InventoryDTO> inventoryDTO) throws EcomException;

    void restoreStock(List<InventoryDTO> inventoryDTO) throws EcomException;

    void deductStockByEvent(ExecutionMessage executionMessage) throws EcomException;

    void restoreStockByEvent(ExecutionMessage executionMessage) throws EcomException;

    void validateStockByEvent(ExecutionMessage executionMessage) throws EcomException;

    void undoValidateStockByEvent(ExecutionMessage executionMessage) throws EcomException;
}
