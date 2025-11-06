package com.ecom.orchestrator.entity;

public enum ExecutionStatusEnum {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    UNDOING,
    UNDONE,
    NOT_REGISTERED,
    DO_SUCCESS,
    DO_FAIL,
    UNDO_SUCCESS,
    UNDO_FAIL,
    RETRY_EXHAUSTED
}
