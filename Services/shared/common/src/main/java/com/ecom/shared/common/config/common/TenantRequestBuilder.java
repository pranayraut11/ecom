package com.ecom.shared.common.config.common;

import java.net.URI;
import java.net.http.HttpRequest;

public class TenantRequestBuilder {
    public static HttpRequest.Builder create(URI uri) {
        return HttpRequest.newBuilder(uri)
                .header("X-Tenant-Id", TenantContext.getTenantId());
    }
}