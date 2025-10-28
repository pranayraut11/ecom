package com.ecom.tenantmanagement.util;

import com.ecom.tenantmanagement.dto.TenantDTO;
import com.ecom.tenantmanagement.entity.TenantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for converting between TenantEntity and TenantDTO
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TenantMapper {

    /**
     * Convert TenantEntity to TenantDTO
     */
    TenantDTO toDTO(TenantEntity entity);

    /**
     * Convert TenantDTO to TenantEntity
     */
    TenantEntity toEntity(TenantDTO dto);
}
