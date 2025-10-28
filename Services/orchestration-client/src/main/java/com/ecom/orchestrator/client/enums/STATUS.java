package com.ecom.orchestrator.client.enums;

import lombok.Getter;

@Getter
public enum STATUS {
    SUCCESS(true),
    FAILURE(false);

    private boolean value;

    STATUS(boolean value) {
        this.value = value;
    }

}
