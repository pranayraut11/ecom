package com.ecom.orchestrator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "orchestration_step_template")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
    public class OrchestrationStepTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private OrchestrationTemplate template;

    @Column(name = "seq", nullable = false)
    private Integer seq;

    @Column(name = "step_name", nullable = false, length = 255)
    private String stepName;

    @Column(name = "object_type", nullable = false, length = 255)
    private String objectType;

    @Column(name = "topic_name", nullable = false, unique = true, length = 255)
    private String topicName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
