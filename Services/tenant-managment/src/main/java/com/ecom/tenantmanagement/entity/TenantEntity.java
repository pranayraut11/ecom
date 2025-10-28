package com.ecom.tenantmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * Tenant Entity for storing tenant metadata in the master schema
 *
 * This entity represents a tenant in the multi-tenant system and is stored
 * in the public.tenants table. Each tenant has its own schema in the database.
 */
@Entity
@Table(name = "tenants", schema = "public")
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE public.tenants SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "tenant_name", nullable = false, unique = true, length = 100)
    private String tenantName;

    @Column(name = "schema_name", nullable = false, unique = true, length = 100)
    private String schemaName;

    @Column(name = "domain", nullable = false, unique = true, length = 255)
    private String domain;

    @Column(name = "contact_email", nullable = false, length = 255)
    private String contactEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TenantStatus status = TenantStatus.ACTIVE;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    /**
     * Enum representing possible tenant statuses
     */
    public enum TenantStatus {
        ACTIVE,
        INACTIVE,
        SUSPENDED
    }
}
