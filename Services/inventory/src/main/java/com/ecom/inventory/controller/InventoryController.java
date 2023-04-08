package com.ecom.inventory.controller;

import com.ecom.inventory.dto.InventoryDTO;
import com.ecom.inventory.entity.ProductInventory;
import com.ecom.inventory.service.specification.InventoryService;
import com.ecom.shared.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public void remove(@RequestBody InventoryDTO inventoryDTO) throws Exception {
        inventoryService.remove(inventoryDTO);
    }
}
