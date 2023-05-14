package com.ecom.inventory.service.implementation;

import com.ecom.inventory.constants.Operation;
import com.ecom.inventory.dto.InventoryDTO;
import com.ecom.inventory.dto.InventoryResponse;
import com.ecom.inventory.entity.ProductInventory;
import com.ecom.inventory.enums.InventoryStatus;
import com.ecom.inventory.repository.InventoryRepository;
import com.ecom.inventory.service.specification.InventoryService;
import com.ecom.shared.common.exception.EcomException;
import com.ecom.shared.common.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InventoryServiceImpl extends BaseService<ProductInventory> implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public List<ProductInventory> getAll() {
        return null;
    }

    @Override
    public ProductInventory get(String id) {
        return inventoryRepository.findById(id).orElseThrow(() -> new EcomException(HttpStatus.NOT_FOUND, "Inventory not found"));
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
    public void add(InventoryDTO inventoryDTO) throws EcomException {
        adjustQuantity(inventoryDTO.getUserId(), inventoryDTO.getProductId(), inventoryDTO.getQuantity(), Operation.ADD);
    }

    private void adjustQuantity(String sellerId, String productId, int quantity, Operation operation) throws EcomException {
        Optional<ProductInventory> productInventoryOptional = inventoryRepository.findByUserIdAndProductId(sellerId, productId);
        if (productInventoryOptional.isPresent()) {
            ProductInventory productInventory = productInventoryOptional.get();
            int quantityDB = productInventory.getQuantity();
            if (Operation.ADD.equals(operation)) {
                quantityDB = quantityDB + quantity;
            } else if (Operation.SUB.equals(operation)) {
                if (quantityDB == 0 || quantity > quantityDB) {
                    throw new EcomException(HttpStatus.NOT_FOUND, "Stock not available");
                }
                quantityDB = quantityDB - quantity;
            }
            productInventory.setQuantity(quantityDB);
            inventoryRepository.save(productInventory);
        } else {
            throw new EcomException(HttpStatus.NOT_FOUND, "Inventory not found");
        }
    }

    @Override
    public InventoryStatus remove(InventoryDTO inventoryDTO) throws EcomException {
        InventoryStatus status = InventoryStatus.AVAILABLE;
        try {
            adjustQuantity(inventoryDTO.getUserId(), inventoryDTO.getProductId(), inventoryDTO.getQuantity(), Operation.SUB);
        } catch (EcomException e) {
            log.info("Stock not Available for product {} ", inventoryDTO.getProductId());
            status = InventoryStatus.UNAVAILABLE;
        }
        return status;
    }

    @Override
    public List<String> checkStockAvailability(List<InventoryDTO> inventoryDTO) {
        List<String> productIds = inventoryDTO.stream().map(InventoryDTO::getProductId).collect(Collectors.toList());
        List<ProductInventory> productInventoryList = inventoryRepository.findByProductIdIn(productIds);
        List<String> stockUnavailableProductIds = new ArrayList<>();
        if (!productInventoryList.isEmpty()) {
            List<ProductInventory> unavailableProducts = productInventoryList.stream().filter(productInventory -> productInventory.getQuantity() <= 0).collect(Collectors.toList());
            if (!unavailableProducts.isEmpty()) {
                List<String> sellerIds = inventoryDTO.stream().map(InventoryDTO::getUserId).collect(Collectors.toList());
                List<ProductInventory> sellerProducts = unavailableProducts.stream().filter(unavailableProduct -> sellerIds.contains(unavailableProduct.getUserId())).collect(Collectors.toList());
                if (!sellerProducts.isEmpty()) {
                    stockUnavailableProductIds = sellerProducts.stream().map(ProductInventory::getProductId).collect(Collectors.toList());
                }
            }
        }
        return stockUnavailableProductIds;
    }


}
