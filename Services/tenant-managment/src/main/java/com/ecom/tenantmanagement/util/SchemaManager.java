package com.ecom.tenantmanagement.util;

import com.ecom.tenantmanagement.exception.SchemaCreationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Schema Manager for creating and managing tenant-specific database schemas
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaManager {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    /**
     * Create a new schema for a tenant
     */
    public void createTenantSchema(String schemaName) {
        try {
            log.info("Creating schema: {}", schemaName);

            String createSchemaSQL = "CREATE SCHEMA IF NOT EXISTS " + schemaName;
            jdbcTemplate.execute(createSchemaSQL);

            log.info("Successfully created schema: {}", schemaName);

        } catch (Exception e) {
            log.error("Failed to create schema: {}", schemaName, e);
            throw new SchemaCreationException("Failed to create schema: " + schemaName, e);
        }
    }

    /**
     * Run Flyway migrations for a specific tenant schema
     */
    public void migrateTenantSchema(String schemaName) {
        try {
            log.info("Running migrations for schema: {}", schemaName);

            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .schemas(schemaName)
                    .locations("classpath:db/migration/tenant")
                    .baselineOnMigrate(true)
                    .load();

            flyway.migrate();

            log.info("Successfully migrated schema: {}", schemaName);

        } catch (Exception e) {
            log.error("Failed to migrate schema: {}", schemaName, e);
            throw new SchemaCreationException("Failed to migrate schema: " + schemaName, e);
        }
    }

    /**
     * Drop a tenant schema (for cleanup operations)
     */
    public void dropTenantSchema(String schemaName) {
        try {
            log.warn("Dropping schema: {}", schemaName);

            String dropSchemaSQL = "DROP SCHEMA IF EXISTS " + schemaName + " CASCADE";
            jdbcTemplate.execute(dropSchemaSQL);

            log.warn("Successfully dropped schema: {}", schemaName);

        } catch (Exception e) {
            log.error("Failed to drop schema: {}", schemaName, e);
            throw new SchemaCreationException("Failed to drop schema: " + schemaName, e);
        }
    }

    /**
     * Check if schema exists
     */
    public boolean schemaExists(String schemaName) {
        try {
            String checkSchemaSQL = "SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name = ?";
            Integer count = jdbcTemplate.queryForObject(checkSchemaSQL, Integer.class, schemaName);
            return count != null && count > 0;
        } catch (Exception e) {
            log.error("Failed to check if schema exists: {}", schemaName, e);
            return false;
        }
    }
}
