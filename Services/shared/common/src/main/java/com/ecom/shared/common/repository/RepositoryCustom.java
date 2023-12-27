package com.ecom.shared.common.repository;

import com.ecom.shared.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;

public interface RepositoryCustom<T> {

    PageResponse findAll(Query query, Pageable pageable, Class<T> clazz);
}
