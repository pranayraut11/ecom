package com.ecom.tenantmanagement.dto;

import com.ecom.tenantmanagement.entity.TenantEntity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating tenant status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private TenantEntity.TenantStatus status;
}
