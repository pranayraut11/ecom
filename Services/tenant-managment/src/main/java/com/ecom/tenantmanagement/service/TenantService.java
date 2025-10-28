package com.ecom.tenantmanagement.service;

import com.ecom.tenantmanagement.dto.TenantCreateRequest;
import com.ecom.tenantmanagement.dto.TenantDTO;
import com.ecom.tenantmanagement.dto.TenantStatusUpdateRequest;
import com.ecom.tenantmanagement.dto.TenantUpdateRequest;
import com.ecom.tenantmanagement.entity.TenantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for Tenant Management operations
 */
public interface TenantService {

    /**
     * Create a new tenant with automatic schema creation
     */
    TenantDTO createTenant(TenantCreateRequest request);

    /**
     * Get tenant by ID
     */
    TenantDTO getTenantById(UUID id);

    /**
     * Get all tenants with optional status filter and pagination
     */
    Page<TenantDTO> getAllTenants(TenantEntity.TenantStatus status, Pageable pageable);

    /**
     * Update tenant information
     */
    TenantDTO updateTenant(UUID id, TenantUpdateRequest request);

    /**
     * Update tenant status
     */
    TenantDTO updateTenantStatus(UUID id, TenantStatusUpdateRequest request);

    /**
     * Soft delete tenant
     */
    void deleteTenant(UUID id);

    /**
     * Check if tenant exists by domain
     */
    boolean existsByDomain(String domain);

    /**
     * Check if tenant exists by name
     */
    boolean existsByTenantName(String tenantName);
}
