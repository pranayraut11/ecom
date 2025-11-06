package com.ecom.orchestrator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orchestration_run")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrchestrationRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flow_id", nullable = false, unique = true, length = 255)
    private String flowId;

    @Column(name = "orch_name", nullable = false, length = 255)
    private String orchName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ExecutionStatusEnum status;

    @Column(name = "correlation_id", length = 255)
    private String correlationId;

    @Column(name = "triggered_by", length = 50)
    @Builder.Default
    private String triggeredBy = "USER";

    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

    @OneToMany(mappedBy = "orchestrationRun", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrchestrationStepRun> stepRuns;

    @PrePersist
    protected void onCreate() {
        this.startedAt = LocalDateTime.now();
        this.lastUpdatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdatedAt = LocalDateTime.now();
    }
}
