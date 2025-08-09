package com.ecom.authprovider.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .info(new Info()
                        .title("Keycloak Management API")
                        .description("REST API for managing Keycloak realms, clients, roles, and users")
                        .version("1.0")
                        .contact(new Contact()
                                .name("eCommerce Team")
                                .email("admin@ecom.com")
                                .url("https://ecom.com"))
                        .license(new License()
                                .name("Internal Use Only")
                                .url("https://ecom.com/license")));
    }
}
