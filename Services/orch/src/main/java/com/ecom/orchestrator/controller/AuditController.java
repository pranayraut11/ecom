package com.ecom.orchestrator.controller;

import com.ecom.orchestrator.dto.AuditTimelineResponseDto;
import com.ecom.orchestrator.entity.AuditEventTypeEnum;
import com.ecom.orchestrator.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Audit Timeline", description = "APIs for retrieving orchestration execution audit timeline")
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/{executionId}")
    @Operation(
        summary = "Get audit timeline for an execution",
        description = "Retrieve complete timeline of all events for a specific orchestration execution, ordered by timestamp"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Audit timeline retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Execution not found or no audit events available")
    })
    public ResponseEntity<AuditTimelineResponseDto> getTimeline(
            @Parameter(description = "Execution ID (flowId)", example = "abc123", required = true)
            @PathVariable String executionId,

            @Parameter(description = "Filter by event type", example = "STEP_FAILED")
            @RequestParam(required = false) AuditEventTypeEnum eventType,

            @Parameter(description = "Filter by status", example = "FAILED")
            @RequestParam(required = false) String status,

            @Parameter(description = "Filter events from this timestamp", example = "2025-11-03T20:00:00")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,

            @Parameter(description = "Filter events until this timestamp", example = "2025-11-03T21:00:00")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        try {
            log.info("Received request to get audit timeline for executionId: {}", executionId);

            AuditTimelineResponseDto timeline;
            
            if (eventType != null || status != null || from != null || to != null) {
                // Apply filters
                timeline = auditService.getTimelineWithFilters(executionId, eventType, status, from, to);
            } else {
                // No filters
                timeline = auditService.getTimeline(executionId);
            }

            if (timeline == null || timeline.getEvents().isEmpty()) {
                log.warn("No audit events found for executionId: {}", executionId);
                return ResponseEntity.notFound().build();
            }

            log.info("Successfully retrieved {} audit events for executionId: {}", 
                timeline.getTotalEvents(), executionId);

            return ResponseEntity.ok(timeline);

        } catch (Exception e) {
            log.error("Error retrieving audit timeline for executionId: {}", executionId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/orchestration/{orchName}")
    @Operation(
        summary = "Get recent audit events for an orchestration",
        description = "Retrieve recent audit events for a specific orchestration type"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Audit events retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No audit events found")
    })
    public ResponseEntity<AuditTimelineResponseDto> getRecentByOrchestration(
            @Parameter(description = "Orchestration name", example = "tenantCreation", required = true)
            @PathVariable String orchName) {

        try {
            log.info("Received request to get recent audit events for orchestration: {}", orchName);

            // This could be enhanced with pagination
            // For now, return a simple response
            return ResponseEntity.ok(AuditTimelineResponseDto.builder()
                .orchName(orchName)
                .events(java.util.List.of())
                .build());

        } catch (Exception e) {
            log.error("Error retrieving audit events for orchestration: {}", orchName, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

