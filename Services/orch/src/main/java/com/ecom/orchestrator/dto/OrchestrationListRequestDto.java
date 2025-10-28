package com.ecom.orchestrator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request parameters for listing orchestrations")
public class OrchestrationListRequestDto {

    @Schema(description = "Page number (0-based)", example = "0", defaultValue = "0")
    @Builder.Default
    private Integer page = 0;

    @Schema(description = "Page size", example = "10", defaultValue = "10")
    @Builder.Default
    private Integer size = 10;

    @Schema(description = "Field to sort by", example = "orchName", allowableValues = {"orchName", "type", "status", "initiatorName", "lastUpdated"})
    private String sortBy;

    @Schema(description = "Sort direction", example = "asc", allowableValues = {"asc", "desc"}, defaultValue = "desc")
    @Builder.Default
    private String direction = "desc";

    @Schema(description = "Filter by status", example = "REGISTERED", allowableValues = {"REGISTERED", "PARTIALLY_REGISTERED", "FAILED"})
    private String status;

    @Schema(description = "Filter by type", example = "SEQUENTIAL", allowableValues = {"SEQUENTIAL", "SIMULTANEOUS"})
    private String type;

    @Schema(description = "Filter by orchestration name (case-insensitive contains)", example = "tenant")
    private String orchName;

    @Schema(description = "Filter by registration date from (ISO format)", example = "2025-10-01T00:00:00")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime registeredFrom;

    @Schema(description = "Filter by registration date to (ISO format)", example = "2025-10-31T23:59:59")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime registeredTo;
}
