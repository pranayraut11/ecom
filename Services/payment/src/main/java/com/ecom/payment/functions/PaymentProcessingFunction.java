package com.ecom.payment.functions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
@Slf4j
public class PaymentProcessingFunction {

    @Bean
    public Function<String, String> createPayment() {
        return paymentRequest -> {
            log.info("Processing payment request: {}", paymentRequest);
            return "SUCCESS";
        };
    }

    @Bean
    Function<String,String> revertPayment() {
        return response -> {
            log.info("Reverting payment request: {}", response);
            return "SUCCESS";
        };
    }
}
