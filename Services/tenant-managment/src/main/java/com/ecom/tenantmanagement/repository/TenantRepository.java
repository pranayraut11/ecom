package com.ecom.tenantmanagement.repository;

import com.ecom.tenantmanagement.entity.TenantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Tenant entities
 *
 * Provides CRUD operations for tenant management with custom queries
 * for multi-tenant operations.
 */
@Repository
public interface TenantRepository extends JpaRepository<TenantEntity, UUID> {

    /**
     * Find tenant by domain name
     */
    Optional<TenantEntity> findByDomain(String domain);

    /**
     * Find tenant by schema name
     */
    Optional<TenantEntity> findBySchemaName(String schemaName);

    /**
     * Find tenant by tenant name
     */
    Optional<TenantEntity> findByTenantName(String tenantName);

    /**
     * Check if domain exists (case-insensitive)
     */
    boolean existsByDomainIgnoreCase(String domain);

    /**
     * Check if tenant name exists (case-insensitive)
     */
    boolean existsByTenantNameIgnoreCase(String tenantName);

    /**
     * Check if schema name exists (case-insensitive)
     */
    boolean existsBySchemaNameIgnoreCase(String schemaName);

    /**
     * Find all tenants with optional status filter
     */
    @Query("SELECT t FROM TenantEntity t WHERE (:status IS NULL OR t.status = :status)")
    Page<TenantEntity> findAllByStatus(@Param("status") TenantEntity.TenantStatus status, Pageable pageable);
   
    /**
     * Find all active tenants
     */
    Page<TenantEntity> findByStatus(TenantEntity.TenantStatus status, Pageable pageable);
}
