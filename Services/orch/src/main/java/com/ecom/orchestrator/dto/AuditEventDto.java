package com.ecom.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Audit event representing a single action in the orchestration timeline")
public class AuditEventDto {

    @Schema(description = "Event ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String id;

    @Schema(description = "Execution ID this event belongs to", example = "abc123")
    private String executionId;

    @Schema(description = "Orchestration name", example = "tenantCreation")
    private String orchName;

    @Schema(description = "Entity type", example = "STEP", allowableValues = {"ORCHESTRATION", "STEP"})
    private String entityType;

    @Schema(description = "Step name (if applicable)", example = "createRealm")
    private String stepName;

    @Schema(description = "Event type", example = "STEP_STARTED")
    private String eventType;

    @Schema(description = "Current status", example = "SUCCESS")
    private String status;

    @Schema(description = "Event timestamp", example = "2025-11-03T20:10:24.123Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;

    @Schema(description = "Reason or error message", example = "Timeout calling Keycloak")
    private String reason;

    @Schema(description = "Additional event details")
    private Map<String, Object> details;

    @Schema(description = "Who/what created this event", example = "tenant-management-service")
    private String createdBy;

    @Schema(description = "Source service name", example = "realm-worker")
    private String serviceName;

    @Schema(description = "Operation type", example = "DO", allowableValues = {"DO", "UNDO"})
    private String operationType;

    @Schema(description = "Duration in milliseconds (if applicable)", example = "1234")
    private Long durationMs;

    @Schema(description = "Retry count (if applicable)", example = "2")
    private Integer retryCount;
}

