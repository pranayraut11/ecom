package com.ecom.orchestrator.mappers;

import com.ecom.shared.contract.dto.OrderOrchestratorRequestDTO;
import com.ecom.shared.contract.dto.PaymentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", implementationPackage = "com.ecom.orchestrator.mappers.implementation")
public interface PaymentMapper {

    PaymentMapper PAYMENT_MAPPER = Mappers.getMapper(PaymentMapper.class);

    PaymentRequest toDTO(OrderOrchestratorRequestDTO orchestratorRequestDTO);
}
