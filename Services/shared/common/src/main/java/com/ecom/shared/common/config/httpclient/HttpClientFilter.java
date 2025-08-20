package com.ecom.shared.common.config.httpclient;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface HttpClientFilter {
    HttpResponse<String> filter(HttpRequest request, FilterChain chain) throws Exception;

    interface FilterChain {
        HttpResponse<String> proceed(HttpRequest request) throws Exception;
    }
}
