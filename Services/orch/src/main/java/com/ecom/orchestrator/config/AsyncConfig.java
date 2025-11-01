package com.ecom.orchestrator.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executor;

/**
 * Configuration for async processing
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Configure thread pool for async operations
     * Separate executor for registration-related async tasks
     */
    @Bean(name = "registrationAsyncExecutor")
    public Executor registrationAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("registration-async-");
        executor.initialize();
        return executor;
    }
}

