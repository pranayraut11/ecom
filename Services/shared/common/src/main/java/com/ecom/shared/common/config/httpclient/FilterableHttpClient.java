package com.ecom.shared.common.config.httpclient;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class FilterableHttpClient {
    private final HttpClient httpClient;
    private final List<HttpClientFilter> filters;

    public FilterableHttpClient(HttpClient httpClient, List<HttpClientFilter> filters) {
        this.httpClient = httpClient;
        this.filters = filters;
    }

    public HttpResponse<String> send(HttpRequest request) throws Exception {
        return new FilterChainImpl(filters, httpClient).proceed(request);
    }

    private static class FilterChainImpl implements HttpClientFilter.FilterChain {
        private final List<HttpClientFilter> filters;
        private final HttpClient httpClient;
        private int index = 0;

        FilterChainImpl(List<HttpClientFilter> filters, HttpClient httpClient) {
            this.filters = filters;
            this.httpClient = httpClient;
        }

        @Override
        public HttpResponse<String> proceed(HttpRequest request) throws Exception {
            if (index < filters.size()) {
                return filters.get(index++).filter(request, this);
            }
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }
}
