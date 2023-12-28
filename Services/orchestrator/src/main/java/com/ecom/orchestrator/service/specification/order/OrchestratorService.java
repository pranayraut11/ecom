package com.ecom.orchestrator.service.specification.order;

import com.ecom.orchestrator.dto.*;
import reactor.core.publisher.Mono;

public interface OrchestratorService {

    void createTransaction(final OrchestratorRequestDTO requestDTO);

}
