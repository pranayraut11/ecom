package com.ecom.shared.common.config.common;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
@Slf4j
public class WebClientConfig {

    public static final int MAX_CONNECTIONS = 100;
    public static final String CONNECTION_PROVIDER_NAME = "WEB_CLIENT_CONNECTION_PROVIDER";

    @Bean
    public WebClient webClient() {
        // Configure memory limitations (16MB instead of default 256KB)
        final int size = 16 * 1024 * 1024;
        HttpClient httpClient = HttpClient.create(ConnectionProvider.create(CONNECTION_PROVIDER_NAME, MAX_CONNECTIONS));
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .filter(tenantIdFilter())
                .build();
    }

    /**
     * Filter function to add tenant ID to outgoing requests
     */
    private ExchangeFilterFunction tenantIdFilter() {
        return (request, next) -> {
            String tenantId = TenantContext.getTenantId();
            if (StringUtils.hasText(tenantId)) {
                log.debug("Adding tenant ID header to outgoing request: {}", tenantId);
                return next.exchange(
                        ClientRequest.from(request)
                                .header(TenantFilter.TENANT_HEADER, tenantId)
                                .build()
                );
            }
            return next.exchange(request);
        };
    }
}
