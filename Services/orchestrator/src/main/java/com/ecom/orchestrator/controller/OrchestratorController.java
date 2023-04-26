package com.ecom.orchestrator.controller;

import com.ecom.orchestrator.dto.OrchestratorRequest;
import com.ecom.orchestrator.dto.OrderRequest;
import com.ecom.orchestrator.dto.OrderResponse;
import com.ecom.orchestrator.service.specification.order.OrchestratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping("orchestrate")
public class OrchestratorController {

    @Autowired
    private OrchestratorService orchestratorService;

    @PostMapping("order")
    public void createOrder(@RequestBody OrderRequest orderRequest) {
          orchestratorService.createTransaction(orderRequest);
    }
}
