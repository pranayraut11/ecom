package com.ecom.tenantmanagement.service;

import com.ecom.orchestrator.client.dto.ExecutionMessage;
import com.ecom.orchestrator.client.service.OrchestrationService;
import com.ecom.tenantmanagement.dto.TenantCreateRequest;
import com.ecom.tenantmanagement.dto.TenantDTO;
import com.ecom.tenantmanagement.dto.TenantStatusUpdateRequest;
import com.ecom.tenantmanagement.dto.TenantUpdateRequest;
import com.ecom.tenantmanagement.entity.TenantEntity;
import com.ecom.tenantmanagement.enums.OrchestrationNames;
import com.ecom.tenantmanagement.exception.SchemaCreationException;
import com.ecom.tenantmanagement.exception.TenantAlreadyExistsException;
import com.ecom.tenantmanagement.exception.TenantNotFoundException;
import com.ecom.tenantmanagement.repository.TenantRepository;
import com.ecom.tenantmanagement.util.TenantMapper;
import com.ecom.tenantmanagement.util.SchemaManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of TenantService for managing tenant operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    private final SchemaManager schemaManager;
    private final OrchestrationService orchestrationService;
    private final ObjectMapper objectMapper;

    @Override
    public TenantDTO createTenant(TenantCreateRequest request) {
        log.info("Creating new tenant: {}", request.getTenantName());

        // Check for duplicates
        validateTenantUniqueness(request);

        // Generate schema name from tenant name
        String schemaName = generateSchemaName(request.getTenantName());

        // Create tenant entity
        TenantEntity tenant = new TenantEntity();
        tenant.setTenantName(request.getTenantName());
        tenant.setSchemaName(schemaName);
        tenant.setDomain(request.getDomain());
        tenant.setContactEmail(request.getContactEmail());
        tenant.setStatus(TenantEntity.TenantStatus.ACTIVE);
        tenant.setIsDeleted(false);

        try {
            // Save tenant to master schema first
            TenantEntity savedTenant = tenantRepository.save(tenant);

            // Create schema for the tenant
            schemaManager.createTenantSchema(schemaName);

            log.info("Successfully created tenant: {} with schema: {}",
                    savedTenant.getTenantName(), savedTenant.getSchemaName());
            Map<String,Object> headers = new HashMap<>();
            HashMap<String,Object> messageHeaders = new HashMap<>(headers);
            headers.put("orchestrationName", OrchestrationNames.CREATE_TENANT.getValue());

            ExecutionMessage savedTenantMessage = ExecutionMessage.builder().payload(savedTenant).headers(messageHeaders).build();
            orchestrationService.startOrchestration(savedTenantMessage, OrchestrationNames.CREATE_TENANT.getValue());
            return tenantMapper.toDTO(savedTenant);

        } catch (Exception e) {
            log.error("Failed to create tenant: {}", e.getMessage(), e);
            // Rollback: delete tenant if schema creation failed
            if (tenant.getId() != null) {
                tenantRepository.deleteById(tenant.getId());
            }
            throw new SchemaCreationException("Failed to create tenant schema", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TenantDTO getTenantById(UUID id) {
        log.debug("Fetching tenant by ID: {}", id);

        TenantEntity tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new TenantNotFoundException("Tenant not found with ID: " + id));

        return tenantMapper.toDTO(tenant);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TenantDTO> getAllTenants(TenantEntity.TenantStatus status, Pageable pageable) {
        log.debug("Fetching tenants with status: {} and pagination: {}", status, pageable);

        Page<TenantEntity> tenants = tenantRepository.findAllByStatus(status, pageable);
        return tenants.map(tenantMapper::toDTO);
    }

    @Override
    public TenantDTO updateTenant(UUID id, TenantUpdateRequest request) {
        log.info("Updating tenant: {}", id);

        TenantEntity tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new TenantNotFoundException("Tenant not found with ID: " + id));

        // Update fields if provided
        if (request.getTenantName() != null) {
            if (!request.getTenantName().equals(tenant.getTenantName()) &&
                tenantRepository.existsByTenantNameIgnoreCase(request.getTenantName())) {
                throw new TenantAlreadyExistsException("Tenant name already exists: " + request.getTenantName());
            }
            tenant.setTenantName(request.getTenantName());
        }

        if (request.getDomain() != null) {
            if (!request.getDomain().equals(tenant.getDomain()) &&
                tenantRepository.existsByDomainIgnoreCase(request.getDomain())) {
                throw new TenantAlreadyExistsException("Domain already exists: " + request.getDomain());
            }
            tenant.setDomain(request.getDomain());
        }

        if (request.getContactEmail() != null) {
            tenant.setContactEmail(request.getContactEmail());
        }

        TenantEntity updatedTenant = tenantRepository.save(tenant);
        log.info("Successfully updated tenant: {}", updatedTenant.getTenantName());

        return tenantMapper.toDTO(updatedTenant);
    }

    @Override
    public TenantDTO updateTenantStatus(UUID id, TenantStatusUpdateRequest request) {
        log.info("Updating tenant status: {} to {}", id, request.getStatus());

        TenantEntity tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new TenantNotFoundException("Tenant not found with ID: " + id));

        tenant.setStatus(request.getStatus());
        TenantEntity updatedTenant = tenantRepository.save(tenant);

        log.info("Successfully updated tenant status: {} to {}",
                updatedTenant.getTenantName(), updatedTenant.getStatus());

        return tenantMapper.toDTO(updatedTenant);
    }

    @Override
    public void deleteTenant(UUID id) {
        log.info("Soft deleting tenant: {}", id);

        TenantEntity tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new TenantNotFoundException("Tenant not found with ID: " + id));

        // Soft delete using @SQLDelete annotation
        tenantRepository.delete(tenant);

        log.info("Successfully soft deleted tenant: {}", tenant.getTenantName());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByDomain(String domain) {
        return tenantRepository.existsByDomainIgnoreCase(domain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTenantName(String tenantName) {
        return tenantRepository.existsByTenantNameIgnoreCase(tenantName);
    }

    private void validateTenantUniqueness(TenantCreateRequest request) {
        if (tenantRepository.existsByTenantNameIgnoreCase(request.getTenantName())) {
            throw new TenantAlreadyExistsException("Tenant name already exists: " + request.getTenantName());
        }

        if (tenantRepository.existsByDomainIgnoreCase(request.getDomain())) {
            throw new TenantAlreadyExistsException("Domain already exists: " + request.getDomain());
        }
    }

    private String generateSchemaName(String tenantName) {
        // Convert tenant name to valid schema name (lowercase, replace spaces with underscores)
        return "tenant_" + tenantName.toLowerCase()
                .replaceAll("[^a-z0-9]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
    }
}
