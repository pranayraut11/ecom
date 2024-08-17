package com.ecom.inventory.functions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
@Slf4j
public class InventoryFunction {

    @Autowired
    private StreamBridge streamBridge;
    @Bean
    public Function<String,String> blockInventory(){
        return products->{
            log.info("Checking product availability..");
           log.info(products);
           return "SUCCESS";
        };

    }

    @Bean
    public Function<String,String> unBlockInventory(){
        return (products)->{
            log.info("Unblocking inventory for product 001.");
            return "SUCCESS";
        };
    }
}
