package com.ecom.orchestrator.service.implementation.order.steps;

import com.ecom.orchestrator.enums.ExecutionOrder;
import com.ecom.orchestrator.service.specification.order.WorkflowStep;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Slf4j
@Service(PaymentStep.BEAN_ID)
@AllArgsConstructor
public class PaymentStep implements WorkflowStep {
    public static final String BEAN_ID = "PaymentStep";
    private StreamBridge streamBridge;



    @Override
    public Boolean process() {
        log.info("Processing Payment");
        streamBridge.send("createPayment-out-0","500");
        return Boolean.TRUE;
    }

    @Override
    public Boolean revert() {
        log.info("Reverting Payment");
        streamBridge.send("revertPayment-out-0","1500");
        return Boolean.TRUE;
    }

    @Override
    public ExecutionOrder getOrder() {
        return ExecutionOrder.SECOND;
    }
}
