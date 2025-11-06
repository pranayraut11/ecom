package com.ecom.orchestrator.entity;

public enum AuditEventTypeEnum {
    // Orchestration-level events
    ORCHESTRATION_STARTED,
    ORCHESTRATION_COMPLETED,
    ORCHESTRATION_FAILED,

    // Step-level events (DO operations)
    STEP_STARTED,
    STEP_SUCCESS,              // Renamed from STEP_COMPLETED
    STEP_FAILED,

    // Retry events
    STEP_RETRY_TRIGGERED,      // Renamed from RETRY_ATTEMPT
    RETRY_EXHAUSTED,

    // Rollback/UNDO events
    ROLLBACK_STARTED,        // When rollback process begins
    ROLLBACK_TRIGGERED,      // When rollback is triggered due to failure
    UNDO_STARTED,
    UNDO_COMPLETED,
    UNDO_FAILED,
    ROLLBACK_COMPLETED,

    // Additional events
    STEP_SKIPPED,
    WORKFLOW_PAUSED,
    WORKFLOW_RESUMED
}

