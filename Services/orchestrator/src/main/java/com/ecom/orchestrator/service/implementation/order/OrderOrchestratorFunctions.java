package com.ecom.orchestrator.service.implementation.order;

import com.ecom.orchestrator.service.specification.order.OrchestratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class OrderOrchestratorFunctions {

    @Autowired
    private OrchestratorService orchestratorService;

    @Bean
    public Consumer<String> blockInventoryStatus(){
       return (products)->{
           log.info("Received inventory {}",products);
           if(products.equalsIgnoreCase("SUCCESS")){
               log.info("Product is available");
           }else {
                log.info("Product not available");
               orchestratorService.undoTransaction("UNDO");
           }
       };
    }

    @Bean
    public Consumer<String> unBlockInventoryStatus(){
        return (response) -> {log.info("Received unBlockInventory response {}",response);

            if(response.equalsIgnoreCase("SUCCESS")) {
                log.info("unBlockInventory is successful");
            }else {
                log.info("unBlockInventory failed");
            }
        };
    }

    @Bean
    public Consumer<String> createPaymentStatus(){
        return (response) -> {log.info("Received payment response {}",response);

        if(response.equalsIgnoreCase("SUCCESS")) {
            log.info("Payment is successful");
        }else {
            log.info("Payment failed");
            orchestratorService.undoTransaction("UNDO");
        }
        };
    }

    @Bean
    public Consumer<String> revertPaymentStatus(){
        return (response) -> {log.info("Received payment revert response {}",response);

            if(response.equalsIgnoreCase("SUCCESS")) {
                log.info("unBlockInventory is successful");
            }else {
                log.info("unBlockInventory failed");
            }
        };
    }

}
