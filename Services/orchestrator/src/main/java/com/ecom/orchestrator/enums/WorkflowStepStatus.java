package com.ecom.orchestrator.enums;

public enum WorkflowStepStatus {
    INITIATED("INITIATED"), IN_PROGRESS("IN_PROGRESS"), COMPLETED("COMPLETED"),FAILED("FAILED");

    private String value;

    WorkflowStepStatus(String value) {
        this.value = value;
    }
}
