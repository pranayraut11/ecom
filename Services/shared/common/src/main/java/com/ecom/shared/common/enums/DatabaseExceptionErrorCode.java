package com.ecom.shared.common.enums;

import lombok.Getter;

@Getter
public enum DatabaseExceptionErrorCode {

    DATABASE_CONNECTION_TIMEOUT("ERR_DB_001", "Database connection timeout");
    private String errorCode;
    private String value;

    DatabaseExceptionErrorCode(String errCode, String message) {
        this.errorCode = errCode;
        this.value = message;
    }
}