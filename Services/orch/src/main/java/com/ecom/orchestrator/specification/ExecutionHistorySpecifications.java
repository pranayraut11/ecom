package com.ecom.orchestrator.specification;

import com.ecom.orchestrator.entity.ExecutionStatusEnum;
import com.ecom.orchestrator.entity.OrchestrationRun;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ExecutionHistorySpecifications {

    private ExecutionHistorySpecifications() {
        // Utility class
    }

    public static Specification<OrchestrationRun> hasOrchName(String orchName) {
        return (root, query, criteriaBuilder) -> 
            orchName == null || orchName.isEmpty() ? null : 
            criteriaBuilder.equal(root.get("orchName"), orchName);
    }

    public static Specification<OrchestrationRun> hasStatus(ExecutionStatusEnum status) {
        return (root, query, criteriaBuilder) -> 
            status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<OrchestrationRun> startedAfter(LocalDateTime fromDate) {
        return (root, query, criteriaBuilder) -> 
            fromDate == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("startedAt"), fromDate);
    }

    public static Specification<OrchestrationRun> startedBefore(LocalDateTime toDate) {
        return (root, query, criteriaBuilder) -> 
            toDate == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("startedAt"), toDate);
    }
}
