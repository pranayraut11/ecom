package com.ecom.orchestrator.service.implementation.order.steps;

import com.ecom.orchestrator.enums.ExecutionOrder;
import com.ecom.orchestrator.service.implementation.order.OrderWorkflowUndo;
import com.ecom.orchestrator.service.specification.order.WorkflowRevertStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service(PaymentRevertStep.BEAN_ID)
@Slf4j
public class PaymentRevertStep implements WorkflowRevertStep {
    public static final String BEAN_ID = "PaymentRevertStep";
    @Autowired
    private StreamBridge streamBridge;
    @Override
    public Boolean revert() {
        log.info("Reverting Payment");
        streamBridge.send("revertPayment-out-0", "1500");
        return Boolean.TRUE;
    }

    @Bean("revertPaymentStatus")
    @Override
    public Consumer<String> revertStatus() {
        return (response) -> {
            log.info("Received payment revert response {}", response);
            if (response.equalsIgnoreCase("SUCCESS")) {
                log.info("unBlockInventory is successful");
            } else {
                log.info("unBlockInventory failed");
            }
        };
    }

    @Override
    public ExecutionOrder getOrder() {
        return ExecutionOrder.SECOND;
    }
}
