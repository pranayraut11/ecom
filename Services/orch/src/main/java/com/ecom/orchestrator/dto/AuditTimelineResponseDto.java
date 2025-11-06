package com.ecom.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Complete audit timeline for an orchestration execution")
public class AuditTimelineResponseDto {

    @Schema(description = "Execution ID", example = "abc123")
    private String executionId;

    @Schema(description = "Orchestration name", example = "tenantCreation")
    private String orchName;

    @Schema(description = "Total number of events", example = "15")
    private Integer totalEvents;

    @Schema(description = "Number of failed events", example = "2")
    private Integer failedEvents;

    @Schema(description = "Number of retry events", example = "3")
    private Integer retryEvents;

    @Schema(description = "List of audit events ordered by timestamp")
    private List<AuditEventDto> events;
}

