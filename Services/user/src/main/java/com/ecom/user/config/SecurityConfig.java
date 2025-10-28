package com.ecom.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll() // Allow access to /auth/login
                        .anyRequest().permitAll() // Require authentication for other requests
                )
                .csrf(csrf -> csrf.disable()); // Disable CSRF if not needed
        return http.build();
    }
}