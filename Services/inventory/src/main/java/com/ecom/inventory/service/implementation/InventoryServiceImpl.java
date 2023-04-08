package com.ecom.inventory.service.implementation;

import com.ecom.inventory.constants.Operation;
import com.ecom.inventory.dto.InventoryDTO;
import com.ecom.inventory.entity.ProductInventory;
import com.ecom.inventory.repository.InventoryRepository;
import com.ecom.inventory.service.specification.InventoryService;
import com.ecom.shared.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryServiceImpl extends BaseService<ProductInventory> implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public List<ProductInventory> getAll() {
        return null;
    }

    @Override
    public ProductInventory get(String id) {
        return inventoryRepository.findById(id).get();
    }

    @Override
    public void delete(String id) {
        inventoryRepository.deleteById(id);
    }

    @Override
    public ProductInventory create(ProductInventory entity) {
        return inventoryRepository.save(entity);
    }

    @Override
    public ProductInventory update(ProductInventory entity) {
        return inventoryRepository.save(entity);
    }

    @Override
    public void add(InventoryDTO inventoryDTO) throws Exception {
        adjustQuantity(inventoryDTO.getUserId(),inventoryDTO.getProductId(),inventoryDTO.getQuantity(),Operation.ADD);
    }

    private void adjustQuantity(String sellerId, String productId, int quantity, Operation operation) throws Exception {
        Optional<ProductInventory> productInventoryOptional = inventoryRepository.findByUserIdAndProductId(sellerId, productId);
        if (productInventoryOptional.isPresent()) {
            ProductInventory productInventory = productInventoryOptional.get();
            int quantityDB = productInventory.getQuantity();
            if(Operation.ADD.equals(operation)) {
                quantityDB = quantityDB + quantity;
            }else {
                quantityDB = quantityDB - quantity;
            }
            productInventory.setQuantity(quantityDB);
            inventoryRepository.save(productInventory);
        }else {
            throw new Exception("Inventory not found");
        }
    }

    @Override
    public void remove(InventoryDTO inventoryDTO) throws Exception {
        adjustQuantity(inventoryDTO.getUserId(),inventoryDTO.getProductId(),inventoryDTO.getQuantity(),Operation.SUB);
    }
}
