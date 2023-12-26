package com.ecom.product.repository;

import com.ecom.product.dto.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;

public interface ProductRepositoryCustom<T> {

    PageResponse findAll(Query query, Pageable pageable, Class<T> clazz);
}
