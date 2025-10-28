package com.ecom.orchestrator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "orchestration_step_run")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrchestrationStepRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orchestration_run_id", nullable = false)
    private OrchestrationRun orchestrationRun;

    @Column(name = "step_name", nullable = false, length = 255)
    private String stepName;

    @Column(name = "seq", nullable = false)
    private Integer seq;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ExecutionStatusEnum status;

    @Column(name = "worker_service", length = 255)
    private String workerService;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "undone_at")
    private LocalDateTime undoneAt;
}
