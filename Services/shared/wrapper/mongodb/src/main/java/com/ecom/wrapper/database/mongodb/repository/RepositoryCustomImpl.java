package com.ecom.wrapper.database.mongodb.repository;

import com.ecom.shared.contract.dto.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RepositoryCustomImpl<T> implements RepositoryCustom<T> {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<T> findAll(Query query, Pageable pageable, Class<T> clazz) {
        long count = mongoTemplate.count(query,clazz);
        query.with(pageable);
        List<T> data = mongoTemplate.find(query, clazz);
        return new PageImpl<>(data,pageable,count);
    }
}
