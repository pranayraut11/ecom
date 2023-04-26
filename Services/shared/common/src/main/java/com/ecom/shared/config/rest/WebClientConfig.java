package com.ecom.shared.config.rest;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.util.TokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.filter(logFilter()).build();
    }


    private ExchangeFilterFunction logFilter() {
        return (clientRequest, next) -> {
            ClientRequest clientRequestCpoy = clientRequest;
            String token = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
            log.info("External Request to {}", clientRequest.url());
            log.info("Token :  {}",token);
            if(StringUtils.hasLength(token)) {
                clientRequestCpoy  = ClientRequest.from(clientRequest)
                        .headers(headers -> {
                            headers.set(HttpHeaders.AUTHORIZATION, token.replaceFirst(TokenUtil.TOKEN_TYPE_BEARER, "").trim());
                        })
                        .build();
            }
            return next.exchange(clientRequestCpoy);
        };
    }
}
