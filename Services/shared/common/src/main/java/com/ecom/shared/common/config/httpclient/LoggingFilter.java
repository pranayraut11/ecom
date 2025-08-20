package com.ecom.shared.common.config.httpclient;

import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
@Slf4j
public class LoggingFilter implements HttpClientFilter {
    @Override
    public HttpResponse<String> filter(HttpRequest request, FilterChain chain) throws Exception {
       log.debug("➡️ Sending request: {}", request.uri());

        HttpResponse<String> response = chain.proceed(request);

        log.debug("⬅️ Response status: {}" , response.statusCode());
        return response;
    }
}
