package com.ecom.wrapper.rest.webclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class WebClientConfig {
    Logger log = LoggerFactory.getLogger(WebClientConfig.class);
    public static final int MAX_CONNECTIONS = 100;
    public static final String CONNECTION_PROVIDER_NAME = "WEB_CLIENT_CONNECTION_PROVIDER";

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        log.info("Creating http connection...");
        HttpClient httpClient = HttpClient.create(ConnectionProvider.create(CONNECTION_PROVIDER_NAME, MAX_CONNECTIONS));
        builder = WebClient.builder().clientConnector( new ReactorClientHttpConnector(httpClient));
        log.info("Http connection created with max connection {}", httpClient.configuration().connectionProvider().maxConnections());
        return builder.build();
    }
}
