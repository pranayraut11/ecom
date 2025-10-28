package com.ecom.orchestrator.repository;

import com.ecom.orchestrator.entity.OrchestrationRun;
import com.ecom.orchestrator.entity.ExecutionStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrchestrationRunRepository extends JpaRepository<OrchestrationRun, Long>, JpaSpecificationExecutor<OrchestrationRun> {

    Optional<OrchestrationRun> findByFlowId(String flowId);

    List<OrchestrationRun> findByOrchName(String orchName);

    List<OrchestrationRun> findByStatus(ExecutionStatusEnum status);

    @Query("SELECT or FROM OrchestrationRun or LEFT JOIN FETCH or.stepRuns WHERE or.flowId = :flowId")
    Optional<OrchestrationRun> findByFlowIdWithSteps(@Param("flowId") String flowId);
}
