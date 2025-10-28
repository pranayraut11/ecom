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

    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "orchestrationRun", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrchestrationStepRun> stepRuns;

    @PrePersist
    protected void onCreate() {
        this.startedAt = LocalDateTime.now();
    }
}
