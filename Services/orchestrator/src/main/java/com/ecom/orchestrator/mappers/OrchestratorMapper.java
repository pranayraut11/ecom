package com.ecom.orchestrator.mappers;

import com.ecom.orchestrator.dto.OrchestratorResponseDTO;
import com.ecom.orchestrator.dto.OrderStatus;
import com.ecom.shared.contract.dto.OrderOrchestratorRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",implementationPackage = "com.ecom.orchestrator.mappers.implementation")
public interface OrchestratorMapper {

    OrchestratorMapper ORCHESTRATOR_MAPPER = Mappers.getMapper(OrchestratorMapper.class);

    OrchestratorResponseDTO toORCResponseDTO(OrderOrchestratorRequestDTO requestDTO, OrderStatus status);
}
