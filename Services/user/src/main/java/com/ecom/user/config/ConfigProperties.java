package com.ecom.user.config;

import com.ecom.user.dto.AuthClientDetails;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigProperties {

    @Bean
    @ConfigurationProperties(prefix = "user.auth")
    public AuthClientDetails authClientDetails(){
        return new AuthClientDetails();
    }
}
