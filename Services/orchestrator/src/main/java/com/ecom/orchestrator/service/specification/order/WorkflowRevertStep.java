package com.ecom.orchestrator.service.specification.order;


import com.ecom.orchestrator.enums.ExecutionOrder;

import java.util.function.Consumer;

public interface WorkflowRevertStep {
    Boolean revert();

    Consumer<String> revertStatus();

    ExecutionOrder getOrder();
}
