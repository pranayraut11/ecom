package com.ecom.orchestrator.repository;

import com.ecom.orchestrator.entity.OrchestrationStepTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrchestrationStepTemplateRepository extends JpaRepository<OrchestrationStepTemplate, Long> {

    List<OrchestrationStepTemplate> findByTemplateOrchNameOrderBySeq(String orchName);

    Optional<OrchestrationStepTemplate> findByTemplateOrchNameAndStepName(String orchName, String stepName);

    boolean existsByTopicName(String topicName);

    boolean existsByTemplateOrchNameAndStepName(String orchName, String stepName);
}
