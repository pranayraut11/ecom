package com.ecom.orchestrator.service.specification.order;

import java.util.List;

public interface WorkflowStart  {
        void start();
        List<WorkflowCreateStep> getWorkflowSteps();
}
