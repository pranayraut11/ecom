package com.ecom.tenantmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity representing a subscription plan
 */
@Entity
@Table(name = "plans", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "plan_id", updatable = false, nullable = false)
    private UUID planId;

    @Column(name = "plan_name", nullable = false, unique = true, length = 100)
    private String planName;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "billing_cycle", nullable = false, length = 20)
    private String billingCycle; // MONTHLY, YEARLY, etc.

    @Column(name = "max_products")
    private Integer maxProducts;

    @Column(name = "max_orders")
    private Integer maxOrders;

    @Column(name = "max_storage")
    private Integer maxStorage; // in MB/GB

    @Lob
    @Column(name = "default_features", columnDefinition = "TEXT")
    private String defaultFeatures; // JSON string
}

