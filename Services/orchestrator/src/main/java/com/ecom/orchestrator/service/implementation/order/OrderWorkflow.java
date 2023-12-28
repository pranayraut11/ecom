package com.ecom.orchestrator.service.implementation.order;

import com.ecom.orchestrator.service.specification.order.Workflow;
import com.ecom.orchestrator.service.specification.order.WorkflowStep;

import java.util.List;


public class OrderWorkflow implements Workflow {

    private List<WorkflowStep> orderWorkflowSteps;


    public OrderWorkflow(List<WorkflowStep> orderWorkflowSteps) {
        this.orderWorkflowSteps = orderWorkflowSteps;
    }

    @Override
    public List<WorkflowStep> getWorkflowSteps() {
        return this.orderWorkflowSteps;
    }

}
