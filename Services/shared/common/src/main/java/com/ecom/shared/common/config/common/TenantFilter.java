package com.ecom.shared.common.config.common;

import com.ecom.shared.common.utility.TokenUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.common.VerificationException;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * Filter to extract the tenant ID from the request headers
 * and set it in the TenantContext for the current thread.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class TenantFilter implements Filter {

    public static final String TENANT_HEADER = "X-Tenant-ID";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            String tenantId = req.getHeader(TENANT_HEADER);

            if (StringUtils.hasText(tenantId)) {
                log.debug("Setting tenant ID: {}", tenantId);
                TenantContext.setTenantId(tenantId);
            } else {
                String authHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    // remove "Bearer " prefix (7 characters)
                    String token = authHeader.substring(7);
                    try {
                        TenantContext.setTenantId(TokenUtils.extractTenantId(token));
                    } catch (VerificationException e) {
                        throw new RuntimeException(e);
                    }
                    log.info("Tenant Id extracted from token");
                    String userId = null; // Implement this method in TokenUtils
                    try {
                        userId = TokenUtils.getUserId(token);
                    } catch (VerificationException e) {
                        throw new RuntimeException(e);
                    }
                    UserContext.setUserId(userId);
                    log.info("User Id extracted from token and set to UserContext");

                }
            }

            chain.doFilter(request, response);
        } finally {
            // Always clear the tenant context after the request is processed
            TenantContext.clear();
            UserContext.clear();
        }
    }
}
