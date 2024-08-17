package com.ecom.order.rest;

import com.ecom.shared.contract.dto.OrderOrchestratorRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("orchestrator")
public interface OrchestratorClient {

    @PostMapping(value = "/order")
    void orchestrateOrder(@RequestBody final OrderOrchestratorRequestDTO orderRequest);

}
