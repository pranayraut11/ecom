package com.ecom.catelog.service.implementation;

import com.ecom.catelog.entity.Product;
import com.ecom.catelog.service.specification.Catelog;
import org.ecom.shared.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CatelogImpl extends BaseService<Product> implements Catelog {
    @Override
    public List<Product> getAll() {
        return Collections.emptyList();
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
        return null;
    }

    @Override
    public Product update(Product entity) {
        return null;
    }
}
