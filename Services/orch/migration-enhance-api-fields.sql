-- Migration to add new fields for enhanced API response
-- Date: 2025-11-03

-- Add new columns to orchestration_run table
ALTER TABLE orchestration_run
ADD COLUMN IF NOT EXISTS correlation_id VARCHAR(255),
ADD COLUMN IF NOT EXISTS triggered_by VARCHAR(50) DEFAULT 'USER',
ADD COLUMN IF NOT EXISTS last_updated_at TIMESTAMP;

-- Update existing records
UPDATE orchestration_run
SET triggered_by = 'USER'
WHERE triggered_by IS NULL;

UPDATE orchestration_run
SET last_updated_at = COALESCE(completed_at, started_at)
WHERE last_updated_at IS NULL;

-- Add new columns to orchestration_step_run table
ALTER TABLE orchestration_step_run
ADD COLUMN IF NOT EXISTS operation_type VARCHAR(20) DEFAULT 'DO',
ADD COLUMN IF NOT EXISTS failure_reason TEXT,
ADD COLUMN IF NOT EXISTS last_retry_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS rollback_triggered BOOLEAN DEFAULT false;

-- Update existing records
UPDATE orchestration_step_run
SET operation_type = 'DO'
WHERE operation_type IS NULL;

UPDATE orchestration_step_run
SET rollback_triggered = false
WHERE rollback_triggered IS NULL;

-- Set operation_type to 'UNDO' for rolled back steps
UPDATE orchestration_step_run
SET operation_type = 'UNDO'
WHERE status IN ('UNDOING', 'UNDO_SUCCESS', 'UNDO_FAIL', 'UNDONE');

-- Set rollback_triggered to true for steps that were rolled back
UPDATE orchestration_step_run
SET rollback_triggered = true
WHERE status IN ('UNDOING', 'UNDO_SUCCESS', 'UNDO_FAIL', 'UNDONE', 'ROLLED_BACK');

-- Copy error_message to failure_reason for existing failed steps
UPDATE orchestration_step_run
SET failure_reason = error_message
WHERE status IN ('FAILED', 'DO_FAIL', 'UNDO_FAIL', 'RETRY_EXHAUSTED')
AND failure_reason IS NULL
AND error_message IS NOT NULL;

-- Add indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_orch_run_correlation_id ON orchestration_run(correlation_id);
CREATE INDEX IF NOT EXISTS idx_orch_run_triggered_by ON orchestration_run(triggered_by);
CREATE INDEX IF NOT EXISTS idx_step_run_operation_type ON orchestration_step_run(operation_type);
CREATE INDEX IF NOT EXISTS idx_step_run_rollback_triggered ON orchestration_step_run(rollback_triggered);

-- Add comments for documentation
COMMENT ON COLUMN orchestration_run.correlation_id IS 'Correlation ID for tracing across services';
COMMENT ON COLUMN orchestration_run.triggered_by IS 'How execution was triggered: USER, SYSTEM, or SCHEDULED';
COMMENT ON COLUMN orchestration_run.last_updated_at IS 'Last update timestamp for this orchestration run';
COMMENT ON COLUMN orchestration_step_run.operation_type IS 'Operation type: DO or UNDO';
COMMENT ON COLUMN orchestration_step_run.failure_reason IS 'Detailed reason for step failure';
COMMENT ON COLUMN orchestration_step_run.last_retry_at IS 'Timestamp of last retry attempt';
COMMENT ON COLUMN orchestration_step_run.rollback_triggered IS 'Whether rollback was triggered for this step';

