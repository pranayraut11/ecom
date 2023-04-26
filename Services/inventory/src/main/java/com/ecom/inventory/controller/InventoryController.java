package com.ecom.inventory.controller;

import com.ecom.inventory.dto.InventoryDTO;
import com.ecom.inventory.dto.InventoryResponse;
import com.ecom.inventory.entity.ProductInventory;
import com.ecom.inventory.service.specification.InventoryService;
import com.ecom.shared.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("inventory")
public class InventoryController extends BaseController<ProductInventory> {

    @Autowired
    private InventoryService inventoryService;


    @PutMapping("add")
    public void add(@RequestBody InventoryDTO inventoryDTO) throws Exception {
        inventoryService.add(inventoryDTO);
    }

    @PutMapping("remove")
    public InventoryResponse remove(@RequestBody InventoryDTO inventoryRequest) throws Exception {
        InventoryResponse inventoryResponse = new InventoryResponse();
        inventoryResponse.setStatus(inventoryService.remove(inventoryRequest));
        return inventoryResponse;
    }

    @PostMapping("available")
    public List<String> checkAvailable(@RequestBody List<InventoryDTO> inventoryRequest) {
        return inventoryService.checkStockAvailability(inventoryRequest);
    }
}
