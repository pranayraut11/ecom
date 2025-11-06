package com.ecom.orchestrator.repository;

import com.ecom.orchestrator.entity.AuditEvent;
import com.ecom.orchestrator.entity.AuditEventTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, String> {

    /**
     * Find all audit events for a specific execution, ordered by timestamp
     */
    List<AuditEvent> findByExecutionIdOrderByTimestampAsc(String executionId);

    /**
     * Find audit events by execution ID with filters
     */
    @Query("SELECT ae FROM AuditEvent ae WHERE ae.executionId = :executionId " +
           "AND (:eventType IS NULL OR ae.eventType = :eventType) " +
           "AND (:status IS NULL OR ae.status = :status) " +
           "AND (:from IS NULL OR ae.timestamp >= :from) " +
           "AND (:to IS NULL OR ae.timestamp <= :to) " +
           "ORDER BY ae.timestamp ASC")
    List<AuditEvent> findByExecutionIdWithFilters(
        @Param("executionId") String executionId,
        @Param("eventType") AuditEventTypeEnum eventType,
        @Param("status") String status,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
    );

    /**
     * Find audit events by orchestration name
     */
    List<AuditEvent> findByOrchNameOrderByTimestampDesc(String orchName);

    /**
     * Find recent audit events for an orchestration
     */
    @Query("SELECT ae FROM AuditEvent ae WHERE ae.orchName = :orchName " +
           "ORDER BY ae.timestamp DESC")
    List<AuditEvent> findRecentByOrchName(@Param("orchName") String orchName);

    /**
     * Count events by execution ID
     */
    long countByExecutionId(String executionId);

    /**
     * Find all failed events for an execution
     */
    @Query("SELECT ae FROM AuditEvent ae WHERE ae.executionId = :executionId " +
           "AND (ae.eventType = 'STEP_FAILED' OR ae.eventType = 'ORCHESTRATION_FAILED' OR ae.eventType = 'UNDO_FAILED') " +
           "ORDER BY ae.timestamp ASC")
    List<AuditEvent> findFailedEventsByExecutionId(@Param("executionId") String executionId);

    /**
     * Delete old audit events (for cleanup/archiving)
     */
    void deleteByTimestampBefore(LocalDateTime timestamp);
}

