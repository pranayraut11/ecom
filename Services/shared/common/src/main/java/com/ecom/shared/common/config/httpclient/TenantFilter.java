package com.ecom.shared.common.config.httpclient;

import com.ecom.shared.common.config.common.TenantContext;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TenantFilter implements HttpClientFilter {
    @Override
    public HttpResponse<String> filter(HttpRequest request, FilterChain chain) throws Exception {
        HttpRequest modified = HttpRequest.newBuilder(request.uri())
                .method(request.method(), request.bodyPublisher().orElse(HttpRequest.BodyPublishers.noBody()))
                .headers(request.headers().map().entrySet().stream()
                        .flatMap(e -> e.getValue().stream().map(v -> new String[]{e.getKey(), v}))
                        .flatMap(java.util.stream.Stream::of)
                        .toArray(String[]::new))
                .header("X-Tenant-Id", TenantContext.getTenantId())
                .build();

        return chain.proceed(modified);
    }
}
