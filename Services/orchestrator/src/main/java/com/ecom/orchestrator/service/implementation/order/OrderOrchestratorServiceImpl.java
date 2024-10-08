package com.ecom.orchestrator.service.implementation.order;

import com.ecom.orchestrator.service.specification.order.OrchestratorService;
import com.ecom.shared.contract.dto.OrderOrchestratorRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderOrchestratorServiceImpl implements OrchestratorService {

    @Autowired
    private OrderWorkflowStart orderWorkflowStart;

    @Override
    public  void createTransaction(final OrderOrchestratorRequestDTO requestDTO) {
        orderWorkflowStart.start();
    }

    @Override
    public void undoTransaction(String requestDTO) {
    }

}
