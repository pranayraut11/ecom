package com.ecom.wrapper.database.mongodb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;

public interface RepositoryCustom<T> {

    Page<T> findAll(Query query, Pageable pageable, Class<T> clazz);
}
