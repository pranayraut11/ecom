package com.ecom.tenantmanagement.multitenancy;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Routing DataSource for Multi-Tenant Support
 *
 * This class extends AbstractRoutingDataSource to provide dynamic routing
 * to different database schemas based on the current tenant context.
 */
public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    /**
     * Determine the current lookup key for routing to the appropriate DataSource
     *
     * @return The current tenant identifier from TenantContext
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getTenantId();
    }
}
