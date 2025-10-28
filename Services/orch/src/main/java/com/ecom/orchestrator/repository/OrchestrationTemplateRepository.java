package com.ecom.orchestrator.repository;

import com.ecom.orchestrator.entity.OrchestrationTemplate;
import com.ecom.orchestrator.entity.OrchestrationStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrchestrationTemplateRepository extends JpaRepository<OrchestrationTemplate, Long> {

    Optional<OrchestrationTemplate> findByOrchName(String orchName);

    boolean existsByOrchName(String orchName);

    List<OrchestrationTemplate> findByStatus(OrchestrationStatusEnum status);

    @Query("SELECT ot FROM OrchestrationTemplate ot LEFT JOIN FETCH ot.steps WHERE ot.orchName = :orchName")
    Optional<OrchestrationTemplate> findByOrchNameWithSteps(@Param("orchName") String orchName);

    // New method for deletion
    void deleteByOrchName(String orchName);
}
