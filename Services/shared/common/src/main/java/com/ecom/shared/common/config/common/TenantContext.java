package com.ecom.shared.common.config.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Thread-local storage for tenant ID information.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TenantContext {
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    /**
     * Set the tenant ID for the current thread.
     *
     * @param tenantId the tenant identifier
     */
    public static void setTenantId(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    /**
     * Get the tenant ID for the current thread.
     *
     * @return the tenant identifier or null if not set
     */
    public static String getTenantId() {
        return CURRENT_TENANT.get();
    }

    /**
     * Clear the tenant ID from the current thread.
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
