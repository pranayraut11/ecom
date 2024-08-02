package com.ecom.shared.common.config.rest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Slf4j
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        String connectionProviderName = "myConnectionProvider";
        log.info("Creating http connection...");
        int maxConnections = 100;
        int acquireTimeout = 1000;
        HttpClient httpClient = HttpClient.create(ConnectionProvider.create(connectionProviderName,maxConnections));
        builder = WebClient.builder().clientConnector( new ReactorClientHttpConnector(httpClient));
        log.info("Http connection created with max connection {}", httpClient.configuration().connectionProvider().maxConnections());
        return builder.filter(logFilter()). build();
    }


    private ExchangeFilterFunction logFilter() {
        return (clientRequest, next) -> {
            ClientRequest clientRequestCpoy = clientRequest;
            Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
            JwtAuthenticationToken oauthToken = (JwtAuthenticationToken) authentication;
            String token = oauthToken.getToken().getTokenValue();
            log.info("External Request to {}", clientRequest.url());
            log.info("Token :  {}",token);
            if(StringUtils.hasLength(token)) {
                clientRequestCpoy  = ClientRequest.from(clientRequest)
                        .headers(headers -> {
                            headers.set(HttpHeaders.AUTHORIZATION, OAuth2AccessToken.TokenType.BEARER.getValue()+" "+ token.trim());
                        })
                        .build();
            }
            return next.exchange(clientRequestCpoy);
        };
    }
}
