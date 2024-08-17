package com.ecom.orchestrator.enums;

import lombok.Getter;

@Getter
public enum ExecutionOrder {
    FIRST(1),
    SECOND(2),
    THIRD(3);
    int value;

    ExecutionOrder(int i) {
    }
}
