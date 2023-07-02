package com.ecom.user.config;

import com.ecom.user.dto.AuthClientDetails;
import com.ecom.user.dto.Login;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigProperties {

    @Bean("userClientCredentials")
    @ConfigurationProperties(prefix = "auth.service")
    public AuthClientDetails authUserClientDetails(){
        return new AuthClientDetails();
    }

    @Bean("adminClientCredentials")
    @ConfigurationProperties(prefix = "auth.admin")
    public AuthClientDetails authAdminClientDetails(){
        return new AuthClientDetails();
    }

    @Bean("adminCredentials")
    @ConfigurationProperties(prefix = "auth.admin")
    public Login authMasterCredentials(){
        return new Login();
    }
}
