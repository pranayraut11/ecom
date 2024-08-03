package com.ecom.product.repository;

import com.ecom.product.entity.Product;
import com.ecom.wrapper.database.mongodb.repository.RepositoryCustom;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product,String>, RepositoryCustom<Product> {

}
