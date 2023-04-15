package com.ecom.orchestrator.controller;

import com.ecom.orchestrator.dto.OrderRequest;
import com.ecom.orchestrator.dto.OrderResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("orchestrate")
public class OrchestratorController {

    @PostMapping("order")
    public OrderResponse createOrder(@RequestBody OrderRequest orderRequest) {
        return null;
    }
}
