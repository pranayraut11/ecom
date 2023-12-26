package com.ecom.product.repository;

import com.ecom.product.entity.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository<T> extends CrudRepository<Product,String>, ProductRepositoryCustom<T> {

}
