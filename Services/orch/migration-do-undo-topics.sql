-- Migration script to add DO/UNDO topics and retry functionality
-- Run this script to update existing orchestrator_db schema

\c orchestrator_db;

-- Add new columns to orchestration_step_template table
ALTER TABLE orchestration_step_template
ADD COLUMN IF NOT EXISTS do_topic VARCHAR(255),
ADD COLUMN IF NOT EXISTS undo_topic VARCHAR(255),
ADD COLUMN IF NOT EXISTS max_retries INTEGER DEFAULT 3 NOT NULL;

-- Update existing rows to populate DO and UNDO topics from existing topic_name
UPDATE orchestration_step_template
SET do_topic = topic_name || '.do',
    undo_topic = topic_name || '.undo'
WHERE do_topic IS NULL OR undo_topic IS NULL;

-- Make do_topic and undo_topic NOT NULL after populating
ALTER TABLE orchestration_step_template
ALTER COLUMN do_topic SET NOT NULL,
ALTER COLUMN undo_topic SET NOT NULL;

-- Add new columns to orchestration_step_run table
ALTER TABLE orchestration_step_run
ADD COLUMN IF NOT EXISTS retry_count INTEGER DEFAULT 0 NOT NULL,
ADD COLUMN IF NOT EXISTS max_retries INTEGER DEFAULT 3 NOT NULL;

-- Add new status values to execution status check constraint
-- First, drop the existing constraint
ALTER TABLE orchestration_run
DROP CONSTRAINT IF EXISTS orchestration_run_status_check;

-- Recreate with new values
ALTER TABLE orchestration_run
ADD CONSTRAINT orchestration_run_status_check
CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'FAILED', 'UNDOING', 'UNDONE', 'NOT_REGISTERED', 'DO_SUCCESS', 'DO_FAIL', 'UNDO_SUCCESS', 'UNDO_FAIL', 'RETRY_EXHAUSTED'));

-- Do the same for orchestration_step_run
ALTER TABLE orchestration_step_run
DROP CONSTRAINT IF EXISTS orchestration_step_run_status_check;

ALTER TABLE orchestration_step_run
ADD CONSTRAINT orchestration_step_run_status_check
CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'FAILED', 'UNDOING', 'UNDONE', 'NOT_REGISTERED', 'DO_SUCCESS', 'DO_FAIL', 'UNDO_SUCCESS', 'UNDO_FAIL', 'RETRY_EXHAUSTED'));

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_step_run_status ON orchestration_step_run(status);
CREATE INDEX IF NOT EXISTS idx_step_run_flowid_stepname ON orchestration_step_run(orchestration_run_id, step_name);
CREATE INDEX IF NOT EXISTS idx_orchestration_run_status ON orchestration_run(status);
CREATE INDEX IF NOT EXISTS idx_orchestration_run_flowid ON orchestration_run(flow_id);

-- Log the migration
DO $$
BEGIN
    RAISE NOTICE 'Migration completed successfully: Added DO/UNDO topics and retry functionality';
END $$;

