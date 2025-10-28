-- Database initialization script for Docker Compose
-- This script will be executed when the PostgreSQL container starts
CREATE DATABASE tenant_management;
CREATE DATABASE orchestrator_db;
-- Ensure the database exists
\c tenant_management;
\c orchestrator_db;
-- Create the update_updated_at_column function if it doesn't exist
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Grant necessary permissions
GRANT ALL PRIVILEGES ON DATABASE tenant_management TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA public TO postgres;
