package com.ecom.inventory.service.implementation;

import com.ecom.inventory.constants.Operation;
import com.ecom.inventory.dto.InventoryDTO;
import com.ecom.inventory.entity.ProductInventory;
import com.ecom.inventory.enums.InventoryStatus;
import com.ecom.inventory.repository.InventoryRepository;
import com.ecom.inventory.service.specification.InventoryService;
import com.ecom.orchestrator.client.dto.ExecutionMessage;
import com.ecom.orchestrator.client.service.OrchestrationService;
import com.ecom.shared.common.exception.EcomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private OrchestrationService orchestrationService;

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

    @Override
    public void deductStock(List<InventoryDTO> inventoryDTO) throws EcomException {
        log.info("Deducting stock for products ...");
        for (InventoryDTO dto : inventoryDTO) {
            adjustQuantity(dto.getUserId(), dto.getProductId(), dto.getQuantity(), Operation.SUB);
        }
        log.info("Stock deduction completed.");
    }

    @Override
    public void restoreStock(List<InventoryDTO> inventoryDTO) throws EcomException {
        log.info("Restoring stock for products ...");
        for (InventoryDTO dto : inventoryDTO) {
            adjustQuantity(dto.getUserId(), dto.getProductId(), dto.getQuantity(), Operation.ADD);
        }
        log.info("Stock restoration completed.");
    }

    @Override
    public void deductStockByEvent(ExecutionMessage executionMessage) throws EcomException {
        log.info("Deducting stock by event ...");
      //  orchestrationService.doNext(executionMessage);
    }

    @Override
    public void restoreStockByEvent(ExecutionMessage executionMessage) throws EcomException {
        log.info("Restoring stock by event ...");
       // orchestrationService.undoNext(executionMessage);
    }

    @Override
    public void validateStockByEvent(ExecutionMessage executionMessage) throws EcomException {
        log.info("Validating stock by event ...");
       // orchestrationService.doNext(executionMessage);
    }

    @Override
    public void undoValidateStockByEvent(ExecutionMessage executionMessage) throws EcomException {
        log.info("Undoing stock validation by event ...");
       // orchestrationService.undoNext(executionMessage);
    }


}
