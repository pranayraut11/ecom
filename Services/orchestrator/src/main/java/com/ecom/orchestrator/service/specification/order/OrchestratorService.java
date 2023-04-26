package com.ecom.orchestrator.service.specification.order;

import com.ecom.orchestrator.dto.OrchestratorRequest;
import com.ecom.orchestrator.dto.OrderRequest;
import com.ecom.orchestrator.dto.OrderResponse;
import reactor.core.publisher.Mono;

public interface OrchestratorService {

    void createTransaction(OrderRequest orderRequest);

}
