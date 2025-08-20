package com.ecom.shared.common.config.httpclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.net.http.HttpClient;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
public class HttpClientConfig {

    @Bean
    public HttpClient baseHttpClient() {
        ThreadFactory virtualThreadFactory = Thread.ofVirtual().name("http-client-", 0).factory();
        return HttpClient.newBuilder().executor(Executors.newThreadPerTaskExecutor(virtualThreadFactory)).build();
        //return HttpClient.newBuilder().build();

    }

    @Bean
    public FilterableHttpClient filterableHttpClient(HttpClient baseHttpClient) {
        return new FilterableHttpClient(baseHttpClient,
                List.of(new TenantFilter(), new LoggingFilter()));
    }
}
