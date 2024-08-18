package com.ecom.orchestrator.service.specification.order;

import com.ecom.orchestrator.enums.ExecutionOrder;

import java.util.function.Consumer;

public interface WorkflowCreateStep {


    Boolean create();

    Consumer<String> createStatus();
    ExecutionOrder getOrder();
}
