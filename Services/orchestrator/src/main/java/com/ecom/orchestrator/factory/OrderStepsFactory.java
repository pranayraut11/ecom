package com.ecom.orchestrator.factory;

import com.ecom.orchestrator.service.specification.order.WorkflowCreateStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderStepsFactory {

    private final Map<String, WorkflowCreateStep> WORKFLOW_STEP_MAP;

    public List<WorkflowCreateStep> getWorkflowSteps() {
        return WORKFLOW_STEP_MAP.values().stream().sorted(Comparator.comparingInt(value -> value.getOrder().getValue())).toList();
    }

}
