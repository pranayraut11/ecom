-- Migration to add audit_event table for orchestration timeline tracking
-- Date: 2025-11-04

-- Create audit_event table
CREATE TABLE IF NOT EXISTS audit_event (
    id VARCHAR(36) PRIMARY KEY,
    execution_id VARCHAR(255) NOT NULL,
    orch_name VARCHAR(255) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    step_name VARCHAR(255),
    event_type VARCHAR(50) NOT NULL,
    status VARCHAR(50),
    timestamp TIMESTAMP NOT NULL,
    reason TEXT,
    details JSONB,
    created_by VARCHAR(255),
    service_name VARCHAR(255),
    operation_type VARCHAR(20),
    duration_ms BIGINT,
    retry_count INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_audit_execution_id ON audit_event(execution_id);
CREATE INDEX IF NOT EXISTS idx_audit_orch_name ON audit_event(orch_name);
CREATE INDEX IF NOT EXISTS idx_audit_timestamp ON audit_event(timestamp);
CREATE INDEX IF NOT EXISTS idx_audit_event_type ON audit_event(event_type);
CREATE INDEX IF NOT EXISTS idx_audit_execution_timestamp ON audit_event(execution_id, timestamp);
CREATE INDEX IF NOT EXISTS idx_audit_entity_type ON audit_event(entity_type);
CREATE INDEX IF NOT EXISTS idx_audit_step_name ON audit_event(step_name) WHERE step_name IS NOT NULL;

-- Add check constraints
ALTER TABLE audit_event
ADD CONSTRAINT chk_entity_type CHECK (entity_type IN ('ORCHESTRATION', 'STEP'));

ALTER TABLE audit_event
ADD CONSTRAINT chk_event_type CHECK (event_type IN (
    'ORCHESTRATION_STARTED', 'ORCHESTRATION_COMPLETED', 'ORCHESTRATION_FAILED',
    'STEP_STARTED', 'STEP_SUCCESS', 'STEP_FAILED',
    'STEP_RETRY_TRIGGERED', 'RETRY_EXHAUSTED',
    'ROLLBACK_STARTED', 'ROLLBACK_TRIGGERED', 'UNDO_STARTED', 'UNDO_COMPLETED', 'UNDO_FAILED', 'ROLLBACK_COMPLETED',
    'STEP_SKIPPED', 'WORKFLOW_PAUSED', 'WORKFLOW_RESUMED'
));

-- Add comments for documentation
COMMENT ON TABLE audit_event IS 'Audit trail for orchestration and step-level events';
COMMENT ON COLUMN audit_event.id IS 'Unique event identifier (UUID)';
COMMENT ON COLUMN audit_event.execution_id IS 'Foreign key to orchestration execution (flowId)';
COMMENT ON COLUMN audit_event.orch_name IS 'Orchestration name';
COMMENT ON COLUMN audit_event.entity_type IS 'Type of entity: ORCHESTRATION or STEP';
COMMENT ON COLUMN audit_event.step_name IS 'Step name (null for orchestration-level events)';
COMMENT ON COLUMN audit_event.event_type IS 'Type of event that occurred';
COMMENT ON COLUMN audit_event.status IS 'Status at the time of event (SUCCESS, FAILED, etc.)';
COMMENT ON COLUMN audit_event.timestamp IS 'When the event occurred';
COMMENT ON COLUMN audit_event.reason IS 'Reason for failure or additional context';
COMMENT ON COLUMN audit_event.details IS 'Additional metadata in JSON format';
COMMENT ON COLUMN audit_event.created_by IS 'Who/what triggered this event';
COMMENT ON COLUMN audit_event.service_name IS 'Source service that sent the event';
COMMENT ON COLUMN audit_event.operation_type IS 'Operation type: DO or UNDO';
COMMENT ON COLUMN audit_event.duration_ms IS 'Duration in milliseconds (for completed events)';
COMMENT ON COLUMN audit_event.retry_count IS 'Retry attempt number (for retry events)';

-- Create a view for easy querying of execution timelines
CREATE OR REPLACE VIEW v_execution_timeline AS
SELECT
    ae.id,
    ae.execution_id,
    ae.orch_name,
    ae.entity_type,
    ae.step_name,
    ae.event_type,
    ae.status,
    ae.timestamp,
    ae.reason,
    ae.details,
    ae.created_by,
    ae.service_name,
    ae.operation_type,
    ae.duration_ms,
    ae.retry_count,
    RANK() OVER (PARTITION BY ae.execution_id ORDER BY ae.timestamp) as event_sequence
FROM audit_event ae
ORDER BY ae.execution_id, ae.timestamp;

COMMENT ON VIEW v_execution_timeline IS 'Timeline view with event sequence numbers for each execution';

-- Optional: Create partition for better performance (PostgreSQL 10+)
-- Partition by month for audit event cleanup
-- Uncomment if you want to implement partitioning:
/*
CREATE TABLE audit_event_template (LIKE audit_event INCLUDING ALL);

CREATE TABLE audit_event_2025_11 PARTITION OF audit_event_template
    FOR VALUES FROM ('2025-11-01') TO ('2025-12-01');

CREATE TABLE audit_event_2025_12 PARTITION OF audit_event_template
    FOR VALUES FROM ('2025-12-01') TO ('2026-01-01');

-- Add more partitions as needed
*/

-- Grant permissions (adjust as needed for your environment)
-- GRANT SELECT ON audit_event TO readonly_user;
-- GRANT SELECT, INSERT ON audit_event TO orchestrator_service;

-- Success message
DO $$
BEGIN
    RAISE NOTICE 'Audit event table created successfully';
    RAISE NOTICE 'Created indexes: 7';
    RAISE NOTICE 'Created view: v_execution_timeline';
END $$;

