package com.ecom.orchestrator.service.implementation.order;

import com.ecom.orchestrator.factory.OrderStepsFactory;
import com.ecom.orchestrator.mappers.InventoryMapper;
import com.ecom.orchestrator.mappers.PaymentMapper;
import com.ecom.orchestrator.service.specification.order.Workflow;
import com.ecom.orchestrator.service.specification.order.WorkflowStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderWorkflow implements Workflow {

    private List<WorkflowStep> orderWorkflowSteps;
    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private StreamBridge streamBridge;

    @Autowired
    private OrderStepsFactory orderStepsFactory;

    @Override
    public List<WorkflowStep> getWorkflowSteps() {
        return orderStepsFactory.getWorkflowSteps();
    }

    @Override
    public void start() {
        orderStepsFactory.getWorkflowSteps().forEach(WorkflowStep::process);
    }

    @Override
    public void undo() {
        orderStepsFactory.getWorkflowSteps().forEach(WorkflowStep::revert);
    }

}
