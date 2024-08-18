package com.ecom.orchestrator.service.implementation.order;

import com.ecom.orchestrator.service.specification.order.WorkflowCreateStep;
import com.ecom.orchestrator.service.specification.order.WorkflowStart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OrderWorkflowStart implements WorkflowStart {
    private final Map<String, WorkflowCreateStep> WORKFLOW_STEP_MAP;
    @Override
    public List<WorkflowCreateStep> getWorkflowSteps() {
        return WORKFLOW_STEP_MAP.values().stream().sorted(Comparator.comparingInt(value -> value.getOrder().getValue())).toList();
    }

    @Override
    public void start() {
        getWorkflowSteps().stream().map(WorkflowCreateStep.class::cast).forEach(WorkflowCreateStep::create);
    }


}
