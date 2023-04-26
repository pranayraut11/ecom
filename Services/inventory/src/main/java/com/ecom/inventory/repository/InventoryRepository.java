package com.ecom.inventory.repository;

import com.ecom.inventory.entity.ProductInventory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends CrudRepository<ProductInventory,String> {

    Optional<ProductInventory> findByUserIdAndProductId(String userId, String productId);

    List<ProductInventory> findByProductIdIn(List<String> productId);
}
