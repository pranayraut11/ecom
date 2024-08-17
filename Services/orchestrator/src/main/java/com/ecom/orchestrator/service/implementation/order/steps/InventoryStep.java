package com.ecom.orchestrator.service.implementation.order.steps;

import com.ecom.orchestrator.enums.ExecutionOrder;
import com.ecom.orchestrator.service.specification.order.WorkflowStep;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Slf4j
@Service(InventoryStep.BEAN_ID)
@AllArgsConstructor
public class InventoryStep implements WorkflowStep {
    public static final String BEAN_ID = "InventoryStep";
    private StreamBridge streamBridge;

    @Override
    public Boolean process() {
        log.info("checking product availability..");
        streamBridge.send("blockInventory-out-0","success");
        return Boolean.TRUE;
    }

    @Override
    public Boolean revert() {
        log.info("unblocking product availability..");
        streamBridge.send("unBlockInventory-out-0","failure");
        return Boolean.TRUE;
    }

    @Override
    public ExecutionOrder getOrder() {
        return ExecutionOrder.FIRST;
    }


}
