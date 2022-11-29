package com.ecom.product.repository.specification;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ecom.product.entity.Product;

@Repository
public interface ProductRepository extends CrudRepository<Product,String> {
}
