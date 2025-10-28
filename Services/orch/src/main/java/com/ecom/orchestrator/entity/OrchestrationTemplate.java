package com.ecom.orchestrator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "orchestration_template")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrchestrationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orch_name", nullable = false, unique = true, length = 255)
    private String orchName;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private OrchestrationTypeEnum type;

    @Column(name = "initiator_service", nullable = false, length = 255)
    private String initiatorService;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private OrchestrationStatusEnum status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "failure_reason", columnDefinition = "jsonb")
    private Map<String, Object> failureReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrchestrationStepTemplate> steps;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
