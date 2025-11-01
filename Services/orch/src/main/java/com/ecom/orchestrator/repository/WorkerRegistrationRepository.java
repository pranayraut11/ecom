package com.ecom.orchestrator.repository;

import com.ecom.orchestrator.entity.WorkerRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerRegistrationRepository extends JpaRepository<WorkerRegistration, Long> {

    List<WorkerRegistration> findByOrchName(String orchName);

    Optional<WorkerRegistration> findByOrchNameAndStepName(String orchName, String stepName);

    List<WorkerRegistration> findByWorkerService(String workerService);

    boolean existsByOrchNameAndStepNameAndWorkerService(String orchName, String stepName, String workerService);

    boolean existsByOrchNameAndStepName(String orchName, String stepName);

    // Count registered workers for an orchestration
    int countByOrchName(String orchName);

    // Count distinct registered steps for an orchestration (optimization for N+1 query)
    @Query("SELECT COUNT(DISTINCT wr.stepName) FROM WorkerRegistration wr WHERE wr.orchName = :orchName")
    long countDistinctStepsByOrchName(@Param("orchName") String orchName);

    // New methods for deletion
    List<WorkerRegistration> findByOrchNameAndWorkerService(String orchName, String workerService);

    @Query("SELECT wr FROM WorkerRegistration wr WHERE wr.orchName = :orchName AND wr.workerService = :workerService AND wr.stepName IN :stepNames")
    List<WorkerRegistration> findByOrchNameAndWorkerServiceAndStepNameIn(
            @Param("orchName") String orchName,
            @Param("workerService") String workerService,
            @Param("stepNames") List<String> stepNames);

    void deleteByOrchNameAndWorkerService(String orchName, String workerService);
}
