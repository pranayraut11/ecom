package com.ecom.catalog.service.implementation;

import com.ecom.catalog.entity.Product;
import com.ecom.catalog.repository.specification.ProductRepository;
import com.ecom.catalog.service.specification.Catalog;
import org.ecom.shared.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CatalogImpl extends BaseService<Product> implements Catalog {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> getAll() {
        List<Product> productList = new ArrayList<>();
        productRepository.findAll().forEach(productList::add);
        return productList;
    }

    @Override
    public Product get(String id) {
        return null;
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public Product create(Product entity) {
        return productRepository.save(entity);
    }

    @Override
    public Product update(Product entity) {
        return null;
    }
}
