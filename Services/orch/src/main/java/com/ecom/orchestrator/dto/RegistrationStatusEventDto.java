package com.ecom.orchestrator.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationStatusEventDto {
    private String eventType; // "REGISTRATION_STATUS"
    private String orchName;
    private String as; // "initiator" or "worker"
    private String serviceName;
    private String status; // "SUCCESS" or "FAILED"
    private Map<String, String> failureReason;
    private LocalDateTime timestamp;
}
