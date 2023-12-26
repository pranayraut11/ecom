package com.ecom.product.repository.implementation;

import com.ecom.product.dto.PageResponse;
import com.ecom.product.repository.ProductRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ProductRepositoryCustomImpl<T> implements ProductRepositoryCustom<T> {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public PageResponse findAll(Query query, Pageable pageable, Class<T> clazz) {
        long count = mongoTemplate.count(query,clazz);
        query.with(pageable);
        List<T> data = mongoTemplate.find(query, clazz);
        Page<T> page = new PageImpl<>(data,pageable,count);
       return PageResponse.builder().data((List<Object>) page.getContent()).last(page.isLast()).first(page.isFirst()).totalPages(page.getTotalPages()).number(page.getNumber()).totalElements(page.getTotalElements()).size(page.getSize()).build();
    }
}
