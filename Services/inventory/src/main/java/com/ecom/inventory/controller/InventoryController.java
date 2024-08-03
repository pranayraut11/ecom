package com.ecom.inventory.controller;

import com.ecom.inventory.dto.InventoryDTO;
import com.ecom.inventory.dto.InventoryResponse;
import com.ecom.inventory.service.specification.InventoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("inventory")
@AllArgsConstructor
public class InventoryController {

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
