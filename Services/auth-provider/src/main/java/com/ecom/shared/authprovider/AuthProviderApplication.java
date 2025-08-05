package com.ecom.shared.authprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import lombok.extern.slf4j.Slf4j;

/**
 * Main application class that serves as the entry point for the Keycloak Admin REST API.
 * This class bootstraps the Spring Boot application.
 */
@SpringBootApplication
@OpenAPIDefinition
@Slf4j
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
