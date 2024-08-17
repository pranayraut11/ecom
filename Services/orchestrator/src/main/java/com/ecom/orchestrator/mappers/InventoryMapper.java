package com.ecom.orchestrator.mappers;

import com.ecom.shared.contract.dto.InventoryRequest;
import com.ecom.shared.contract.dto.OrderOrchestratorRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", implementationPackage = "com.ecom.orchestrator.mappers.implementation")
public interface InventoryMapper {
    InventoryMapper INVENTORY_MAPPER = Mappers.getMapper(InventoryMapper.class);


    @Mapping(target = ".", source = "dto.inventory")
    InventoryRequest toDTO(OrderOrchestratorRequestDTO dto);
}
