package com.ecom.orchestrator.service.specification.order;

import com.ecom.orchestrator.enums.ExecutionOrder;

public interface WorkflowStep {
    ExecutionOrder getOrder();

}
