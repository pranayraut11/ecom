package com.ecom.tenantmanagement.multitenancy;

/**
 * Thread-local context to store current tenant information
 *
 * This class manages the current tenant context using ThreadLocal storage,
 * ensuring that each request thread has its own tenant context.
 */
public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    /**
     * Set the current tenant identifier
     */
    public static void setTenantId(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    /**
     * Get the current tenant identifier
     */
    public static String getTenantId() {
        return CURRENT_TENANT.get();
    }

    /**
     * Clear the current tenant context
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }

    /**
     * Check if tenant context is set
     */
    public static boolean hasTenant() {
        return CURRENT_TENANT.get() != null;
    }
}
