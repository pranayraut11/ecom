package com.ecom.tenantmanagement.dto;

import com.ecom.tenantmanagement.entity.TenantEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Tenant response data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantDTO {

    private UUID id;
    private String tenantName;
    private String schemaName;
    private String domain;
    private String contactEmail;
    private TenantEntity.TenantStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
