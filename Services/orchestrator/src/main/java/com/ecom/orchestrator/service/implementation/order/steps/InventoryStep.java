package com.ecom.orchestrator.service.implementation.order.steps;

import com.ecom.orchestrator.enums.ExecutionOrder;
import com.ecom.orchestrator.service.implementation.order.OrderWorkflowUndo;
import com.ecom.orchestrator.service.specification.order.WorkflowCreateStep;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Slf4j
@Service(InventoryStep.BEAN_ID)
@AllArgsConstructor
public class InventoryStep implements WorkflowCreateStep {
    public static final String BEAN_ID = "InventoryStep";
    private StreamBridge streamBridge;
    private OrderWorkflowUndo workflowUndo;

    @Override
    public Boolean create() {
        log.info("checking product availability..");
        streamBridge.send("blockInventory-out-0","success");
        return Boolean.TRUE;
    }

    @Bean("blockInventoryStatus")
    @Override
    public Consumer<String> createStatus() {
        return (products)->{
            log.info("Received inventory {}",products);
            if(products.equalsIgnoreCase("SUCCESS")){
                log.info("Product is available");
            }else {
                log.info("Product not available");
                workflowUndo.undo();
            }
        };
    }

    @Override
    public ExecutionOrder getOrder() {
        return ExecutionOrder.FIRST;
    }


}
