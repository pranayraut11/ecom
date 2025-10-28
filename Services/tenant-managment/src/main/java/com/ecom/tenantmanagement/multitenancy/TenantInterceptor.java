package com.ecom.tenantmanagement.multitenancy;

import com.ecom.tenantmanagement.repository.TenantRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor to resolve and set tenant context for incoming requests
 *
 * This interceptor examines incoming requests to determine the tenant
 * and sets the appropriate context for multi-tenant operations.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TenantInterceptor implements HandlerInterceptor {

    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String TENANT_DOMAIN_HEADER = "X-Tenant-Domain";

    @Lazy
    private final TenantRepository tenantRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Skip tenant resolution for tenant management APIs and public endpoints
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/v1/tenants") ||
            requestURI.startsWith("/actuator") ||
            requestURI.startsWith("/swagger") ||
            requestURI.startsWith("/v3/api-docs")) {
            return true;
        }

        String tenantId = resolveTenantId(request);
        if (tenantId != null) {
            TenantContext.setTenantId(tenantId);
            log.debug("Set tenant context: {}", tenantId);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Clear tenant context after request completion
        TenantContext.clear();
    }

    /**
     * Resolve tenant ID from request headers or domain
     */
    private String resolveTenantId(HttpServletRequest request) {
        // Try to get tenant ID from header
        String tenantId = request.getHeader(TENANT_HEADER);
        if (tenantId != null && !tenantId.trim().isEmpty()) {
            return tenantId.trim();
        }

        // Try to get tenant by domain header
        String domain = request.getHeader(TENANT_DOMAIN_HEADER);
        if (domain != null && !domain.trim().isEmpty()) {
            return tenantRepository.findByDomain(domain.trim())
                .map(tenant -> tenant.getSchemaName())
                .orElse(null);
        }

        // Could also resolve by subdomain from Host header
        String host = request.getHeader("Host");
        if (host != null && host.contains(".")) {
            String subdomain = host.split("\\.")[0];
            return tenantRepository.findByDomain(host)
                .map(tenant -> tenant.getSchemaName())
                .orElse(null);
        }

        return null;
    }
}
