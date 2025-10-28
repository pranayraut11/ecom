package com.ecom.orchestrator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "worker_registration",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_worker_registration",
           columnNames = {"orch_name", "step_name", "worker_service"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orch_name", nullable = false, length = 255)
    private String orchName;

    @Column(name = "step_name", nullable = false, length = 255)
    private String stepName;

    @Column(name = "worker_service", nullable = false, length = 255)
    private String workerService;

    @Column(name = "topic_name", nullable = false, length = 255)
    private String topicName;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private LocalDateTime registeredAt;

    @PrePersist
    protected void onCreate() {
        this.registeredAt = LocalDateTime.now();
    }
}
