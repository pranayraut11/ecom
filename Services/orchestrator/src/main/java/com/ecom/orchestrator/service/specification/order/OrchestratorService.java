package com.ecom.orchestrator.service.specification.order;

import com.ecom.shared.contract.dto.OrderOrchestratorRequestDTO;

public interface OrchestratorService {

    void createTransaction(final OrderOrchestratorRequestDTO requestDTO);
    void undoTransaction(final String requestDTO);
}
