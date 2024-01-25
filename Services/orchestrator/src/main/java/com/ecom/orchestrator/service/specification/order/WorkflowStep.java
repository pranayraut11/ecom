package com.ecom.orchestrator.service.specification.order;

import com.ecom.orchestrator.enums.WorkflowStepStatus;
import reactor.core.publisher.Mono;

public interface WorkflowStep {

    WorkflowStepStatus getStatus();
    Mono<Boolean> process();

    Mono<Boolean> revert();
}
