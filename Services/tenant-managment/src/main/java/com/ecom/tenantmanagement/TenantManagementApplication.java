package com.ecom.tenantmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main Spring Boot Application for Tenant Management Service
 *
 * This microservice provides multi-tenant management capabilities using
 * the "one schema per tenant" strategy with PostgreSQL.
 */
@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = {"com.ecom.*"})
public class TenantManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenantManagementApplication.class, args);
    }
}
