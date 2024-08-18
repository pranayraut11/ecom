package com.ecom.orchestrator.service.specification.order;

import java.util.List;

public interface WorkflowRevert {
    void undo();
    List<WorkflowRevertStep> getWorkflowSteps();
}
