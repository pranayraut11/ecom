package com.ecom.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Timeline event in orchestration execution")
public class TimelineEventDto {

    @Schema(description = "Event timestamp", example = "2025-11-03T19:05:52.123Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;

    @Schema(description = "Event type", example = "STEP_STARTED", allowableValues = {
        "ORCHESTRATION_STARTED", "STEP_STARTED", "STEP_COMPLETED", "STEP_FAILED",
        "STEP_RETRYING", "ROLLBACK_TRIGGERED", "ROLLBACK_STARTED", "ROLLBACK_COMPLETED",
        "ORCHESTRATION_COMPLETED", "ORCHESTRATION_FAILED"
    })
    private String event;

    @Schema(description = "Step name (if applicable)", example = "createRealm")
    private String step;

    @Schema(description = "Event status (if applicable)", example = "DO_SUCCESS")
    private String status;

    @Schema(description = "Event reason or message", example = "Client already exists")
    private String reason;

    @Schema(description = "Additional details", example = "Worker: realm-service")
    private String details;
}

