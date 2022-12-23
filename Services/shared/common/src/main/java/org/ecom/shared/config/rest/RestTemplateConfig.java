package org.ecom.shared.config.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        List<ClientHttpRequestInterceptor> requestInterceptors = restTemplate.getInterceptors();
        if(requestInterceptors.isEmpty()){
            requestInterceptors = new ArrayList<>();
        }
        requestInterceptors.add(new RestTemplateHeaderModifierInterceptor());
        restTemplate.setInterceptors(requestInterceptors);
        return restTemplate;

    }
}
