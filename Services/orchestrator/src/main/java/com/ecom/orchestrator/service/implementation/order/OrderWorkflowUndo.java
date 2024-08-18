package com.ecom.orchestrator.service.implementation.order;

import com.ecom.orchestrator.service.specification.order.WorkflowRevertStep;
import com.ecom.orchestrator.service.specification.order.WorkflowRevert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OrderWorkflowUndo implements WorkflowRevert {
    private final Map<String, WorkflowRevertStep> WORKFLOW_STEP_MAP;
    @Override
    public List<WorkflowRevertStep> getWorkflowSteps() {
        return WORKFLOW_STEP_MAP.values().stream().sorted(Comparator.comparingInt(value -> value.getOrder().getValue())).toList();
    }

    @Override
    public void undo() {
        getWorkflowSteps().forEach(WorkflowRevertStep::revert);
    }


}