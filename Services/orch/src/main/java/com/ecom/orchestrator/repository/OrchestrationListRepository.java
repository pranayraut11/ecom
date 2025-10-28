package com.ecom.orchestrator.repository;

import com.ecom.orchestrator.entity.OrchestrationTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrchestrationListRepository extends JpaRepository<OrchestrationTemplate, Long>, JpaSpecificationExecutor<OrchestrationTemplate> {

    @Query("""
        SELECT ot, 
               COUNT(wr.id) as registeredCount,
               COUNT(st.id) as totalExpected
        FROM OrchestrationTemplate ot
        LEFT JOIN ot.steps st
        LEFT JOIN WorkerRegistration wr ON wr.orchName = ot.orchName AND wr.stepName = st.stepName
        WHERE ot.orchName = :orchName
        GROUP BY ot.id
        """)
    Object[] findOrchestrationWithWorkerCounts(@Param("orchName") String orchName);

    Page<OrchestrationTemplate> findAll(Specification<OrchestrationTemplate> spec, Pageable pageable);
}
