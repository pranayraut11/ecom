package com.ecom.orchestrator.service.implementation.order.steps;

import com.ecom.orchestrator.enums.ExecutionOrder;
import com.ecom.orchestrator.service.specification.order.WorkflowCreateStep;
import com.ecom.orchestrator.service.specification.order.WorkflowRevert;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Slf4j
@Service(PaymentStep.BEAN_ID)
@AllArgsConstructor
public class PaymentStep implements WorkflowCreateStep {
    public static final String BEAN_ID = "PaymentStep";
    private StreamBridge streamBridge;
    private WorkflowRevert orderWorkflow;

    @Override
    public Boolean create() {
        log.info("Processing Payment");
        streamBridge.send("createPayment-out-0", "500");
        return Boolean.TRUE;
    }


    @Bean("createPaymentStatus")
    @Override
    public Consumer<String> createStatus() {
        return (response) -> {
            log.info("Received payment response {}", response);

            if (response.equalsIgnoreCase("SUCCESS")) {
                log.info("Payment is successful");
            } else {
                log.info("Payment failed");
                orderWorkflow.undo();
            }
        };
    }


    @Override
    public ExecutionOrder getOrder() {
        return ExecutionOrder.SECOND;
    }
}
