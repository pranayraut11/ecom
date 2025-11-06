package com.ecom.orchestrator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Entity to capture all orchestration and step-level audit events.
 * Provides a complete execution timeline for monitoring and debugging.
 */
@Entity
@Table(name = "audit_event", indexes = {
    @Index(name = "idx_audit_execution_id", columnList = "execution_id"),
    @Index(name = "idx_audit_orch_name", columnList = "orch_name"),
    @Index(name = "idx_audit_timestamp", columnList = "timestamp"),
    @Index(name = "idx_audit_event_type", columnList = "event_type"),
    @Index(name = "idx_audit_execution_timestamp", columnList = "execution_id, timestamp")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "execution_id", nullable = false, length = 255)
    private String executionId;

    @Column(name = "orch_name", nullable = false, length = 255)
    private String orchName;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 50)
    private AuditEntityTypeEnum entityType;

    @Column(name = "step_name", length = 255)
    private String stepName;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private AuditEventTypeEnum eventType;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "details", columnDefinition = "jsonb")
    private Map<String, Object> details;

    @Column(name = "created_by", length = 255)
    private String createdBy;

    @Column(name = "service_name", length = 255)
    private String serviceName;

    @Column(name = "operation_type", length = 20)
    private String operationType; // DO, UNDO

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "retry_count")
    private Integer retryCount;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }
}

