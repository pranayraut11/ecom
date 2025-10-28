package com.ecom.authprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * Main application class that serves as the entry point for the Keycloak Admin REST API.
 * This class bootstraps the Spring Boot application.
 */
@SpringBootApplication
@OpenAPIDefinition
@Slf4j
@ComponentScan(basePackages = {"com.ecom.*"})
public class AuthProviderApplication {

    /**
     * Main method to start the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthProviderApplication.class, args);
        log.info("Keycloak Admin API started successfully");
    }
}
