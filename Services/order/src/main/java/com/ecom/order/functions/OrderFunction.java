package com.ecom.order.functions;

import com.ecom.order.dto.CreateOrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Configuration
public class OrderFunction {

    @Bean
    public Function< CreateOrderDTO, List<String>> createOrder(){
        return (order)->{
            log.info("Create order ");
            return Collections.singletonList("123");
        };
    }
}
