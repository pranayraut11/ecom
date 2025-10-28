package com.ecom.tenantmanagement.enums;

public enum OrchestrationNames {
    CREATE_TENANT("tenantCreation");

    private String value;

    OrchestrationNames(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
