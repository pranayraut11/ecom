package com.ecom.orchestrator.controller;

import com.ecom.orchestrator.service.specification.order.OrchestratorService;
import com.ecom.shared.common.dto.OrderOrchestratorRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("orchestrate")
public class OrchestratorController {

    @Autowired
    private OrchestratorService orchestratorService;

    @PostMapping("order")
    public void createOrder(@RequestBody final OrderOrchestratorRequestDTO orderRequest) {
          orchestratorService.createTransaction(orderRequest);
    }
}
