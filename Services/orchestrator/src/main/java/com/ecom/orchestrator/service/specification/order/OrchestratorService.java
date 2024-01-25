package com.ecom.orchestrator.service.specification.order;

import com.ecom.shared.common.dto.OrderOrchestratorRequestDTO;

public interface OrchestratorService {

    void createTransaction(final OrderOrchestratorRequestDTO requestDTO);

}
