package com.ecom.orchestrator.controller;

import com.ecom.orchestrator.service.specification.order.OrchestratorService;
import com.ecom.shared.contract.dto.OrderOrchestratorRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
@Slf4j
public class OrderOrchestratorController {

    @Autowired
    private OrchestratorService orchestratorService;

    @PostMapping()
    public void createOrder(@RequestBody final OrderOrchestratorRequestDTO orderRequest) {
        log.info("Starting orchestration for order {} ...",orderRequest.getOrderId());
        orchestratorService.createTransaction(orderRequest);
    }
}
