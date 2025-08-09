package com.ecom.authprovider.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiGenericResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ApiGenericResponse<T> success(String message, T data) {
        return ApiGenericResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiGenericResponse<T> success(String message) {
        return success(message, null);
    }

    public static <T> ApiGenericResponse<T> error(String message) {
        return ApiGenericResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
