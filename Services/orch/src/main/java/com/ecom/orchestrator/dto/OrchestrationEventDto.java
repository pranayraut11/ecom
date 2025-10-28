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
public class OrchestrationEventDto {
    private String flowId;
    private String orchName;
    private String stepName;
    private String action; // "DO" or "UNDO"
    private byte[] payload;
    private Map<String, Object> metadata;
}
