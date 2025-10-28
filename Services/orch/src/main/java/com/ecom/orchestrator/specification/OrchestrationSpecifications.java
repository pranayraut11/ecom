package com.ecom.orchestrator.specification;

import com.ecom.orchestrator.entity.OrchestrationStatusEnum;
import com.ecom.orchestrator.entity.OrchestrationTemplate;
import com.ecom.orchestrator.entity.OrchestrationTypeEnum;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrchestrationSpecifications {

    public static Specification<OrchestrationTemplate> hasStatus(OrchestrationStatusEnum status) {
        return (root, query, criteriaBuilder) ->
            status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<OrchestrationTemplate> hasType(OrchestrationTypeEnum type) {
        return (root, query, criteriaBuilder) ->
            type == null ? null : criteriaBuilder.equal(root.get("type"), type);
    }

    public static Specification<OrchestrationTemplate> orchNameContains(String orchName) {
        return (root, query, criteriaBuilder) ->
            orchName == null || orchName.isEmpty() ? null :
            criteriaBuilder.like(criteriaBuilder.lower(root.get("orchName")),
                "%" + orchName.toLowerCase() + "%");
    }

    public static Specification<OrchestrationTemplate> createdAfter(LocalDateTime from) {
        return (root, query, criteriaBuilder) ->
            from == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<OrchestrationTemplate> createdBefore(LocalDateTime to) {
        return (root, query, criteriaBuilder) ->
            to == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), to);
    }
}
