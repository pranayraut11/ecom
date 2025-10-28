package com.ecom.tenantmanagement.controller;

import com.ecom.tenantmanagement.dto.ApiResponse;
import com.ecom.tenantmanagement.dto.TenantCreateRequest;
import com.ecom.tenantmanagement.dto.TenantDTO;
import com.ecom.tenantmanagement.dto.TenantStatusUpdateRequest;
import com.ecom.tenantmanagement.dto.TenantUpdateRequest;
import com.ecom.tenantmanagement.entity.TenantEntity;
import com.ecom.tenantmanagement.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for Tenant Management operations
 *
 * Provides endpoints for managing tenants in a multi-tenant system
 * with automatic schema creation and management.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
@Tag(name = "Tenant Management", description = "APIs for managing tenants in multi-tenant system")
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    @Operation(
        summary = "Create a new tenant",
        description = "Creates a new tenant with automatic PostgreSQL schema creation and migration"
    )
    public ResponseEntity<ApiResponse<TenantDTO>> createTenant(
            @Valid @RequestBody TenantCreateRequest request) {

        log.info("Creating tenant: {}", request.getTenantName());
        TenantDTO tenant = tenantService.createTenant(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tenant created successfully", tenant));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get tenant by ID",
        description = "Retrieves tenant information by tenant ID"
    )
    public ResponseEntity<ApiResponse<TenantDTO>> getTenantById(
            @Parameter(description = "Tenant ID") @PathVariable UUID id) {

        log.debug("Fetching tenant by ID: {}", id);
        TenantDTO tenant = tenantService.getTenantById(id);

        return ResponseEntity.ok(ApiResponse.success(tenant));
    }

    @GetMapping
    @Operation(
        summary = "Get all tenants",
        description = "Retrieves all tenants with optional status filter and pagination"
    )
    public ResponseEntity<ApiResponse<Page<TenantDTO>>> getAllTenants(
            @Parameter(description = "Filter by tenant status")
            @RequestParam(required = false) TenantEntity.TenantStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        log.debug("Fetching tenants with status: {} and pagination: {}", status, pageable);
        Page<TenantDTO> tenants = tenantService.getAllTenants(status, pageable);

        return ResponseEntity.ok(ApiResponse.success(tenants));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update tenant information",
        description = "Updates tenant information (name, domain, contact email)"
    )
    public ResponseEntity<ApiResponse<TenantDTO>> updateTenant(
            @Parameter(description = "Tenant ID") @PathVariable UUID id,
            @Valid @RequestBody TenantUpdateRequest request) {

        log.info("Updating tenant: {}", id);
        TenantDTO tenant = tenantService.updateTenant(id, request);

        return ResponseEntity.ok(ApiResponse.success("Tenant updated successfully", tenant));
    }

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Update tenant status",
        description = "Updates tenant status (ACTIVE, INACTIVE, SUSPENDED)"
    )
    public ResponseEntity<ApiResponse<TenantDTO>> updateTenantStatus(
            @Parameter(description = "Tenant ID") @PathVariable UUID id,
            @Valid @RequestBody TenantStatusUpdateRequest request) {

        log.info("Updating tenant status: {} to {}", id, request.getStatus());
        TenantDTO tenant = tenantService.updateTenantStatus(id, request);

        return ResponseEntity.ok(ApiResponse.success("Tenant status updated successfully", tenant));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete tenant",
        description = "Soft deletes a tenant (sets isDeleted=true)"
    )
    public ResponseEntity<ApiResponse<Void>> deleteTenant(
            @Parameter(description = "Tenant ID") @PathVariable UUID id) {

        log.info("Deleting tenant: {}", id);
        tenantService.deleteTenant(id);

        return ResponseEntity.ok(ApiResponse.success("Tenant deleted successfully", null));
    }

    @GetMapping("/exists/domain/{domain}")
    @Operation(
        summary = "Check if domain exists",
        description = "Checks if a domain is already registered by another tenant"
    )
    public ResponseEntity<ApiResponse<Boolean>> checkDomainExists(
            @Parameter(description = "Domain to check") @PathVariable String domain) {

        boolean exists = tenantService.existsByDomain(domain);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/exists/name/{tenantName}")
    @Operation(
        summary = "Check if tenant name exists",
        description = "Checks if a tenant name is already registered"
    )
    public ResponseEntity<ApiResponse<Boolean>> checkTenantNameExists(
            @Parameter(description = "Tenant name to check") @PathVariable String tenantName) {

        boolean exists = tenantService.existsByTenantName(tenantName);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
}
