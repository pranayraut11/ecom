package com.ecom.orchestrator.service.specification.order;

import java.util.List;

public interface Workflow {

    List<WorkflowStep> getWorkflowSteps();

    void start();

    void undo();
}
