package com.ecom.orchestrator.client.autoconfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.ecom.orchestration.client")
@Slf4j
public class OrchestrationAutoConfiguration {
}
