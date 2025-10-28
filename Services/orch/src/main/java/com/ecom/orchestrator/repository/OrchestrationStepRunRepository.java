package com.ecom.orchestrator.repository;

import com.ecom.orchestrator.entity.OrchestrationStepRun;
import com.ecom.orchestrator.entity.ExecutionStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrchestrationStepRunRepository extends JpaRepository<OrchestrationStepRun, Long> {

    List<OrchestrationStepRun> findByOrchestrationRunFlowIdOrderBySeq(String flowId);

    Optional<OrchestrationStepRun> findByOrchestrationRunFlowIdAndStepName(String flowId, String stepName);

    List<OrchestrationStepRun> findByStatus(ExecutionStatusEnum status);

    List<OrchestrationStepRun> findByOrchestrationRunFlowIdAndStatus(String flowId, ExecutionStatusEnum status);
}
