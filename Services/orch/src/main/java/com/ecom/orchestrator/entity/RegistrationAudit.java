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

@Entity
@Table(name = "registration_audit")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orch_name", nullable = false, length = 255)
    private String orchName;

    @Enumerated(EnumType.STRING)
    @Column(name = "as_role", nullable = false, length = 50)
    private RegistrationRoleEnum asRole;

    @Column(name = "service_name", nullable = false, length = 255)
    private String serviceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private RegistrationStatusEnum status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "registered_steps", columnDefinition = "jsonb")
    private Map<String, Object> registeredSteps;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "failed_steps", columnDefinition = "jsonb")
    private Map<String, Object> failedSteps;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private LocalDateTime registeredAt;

    @PrePersist
    protected void onCreate() {
        this.registeredAt = LocalDateTime.now();
    }
}
