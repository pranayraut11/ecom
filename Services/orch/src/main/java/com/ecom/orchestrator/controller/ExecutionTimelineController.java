package com.ecom.orchestrator.controller;

import com.ecom.orchestrator.dto.TimelineEventDto;
import com.ecom.orchestrator.service.ExecutionTimelineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orchestrations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Execution Timeline", description = "APIs for retrieving orchestration execution timeline events")
public class ExecutionTimelineController {

    private final ExecutionTimelineService executionTimelineService;

    @GetMapping("/{orchName}/executions/{executionId}/timeline")
    @Operation(
        summary = "Get timeline events for an execution",
        description = "Retrieve timeline events for a specific orchestration execution, ordered by timestamp"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Timeline events retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No timeline events found")
    })
    public ResponseEntity<List<TimelineEventDto>> getTimeline(
            @Parameter(description = "Orchestration name", example = "tenantCreation", required = true)
            @PathVariable String orchName,
            @Parameter(description = "Execution ID (flowId)", example = "abc123", required = true)
            @PathVariable String executionId) {
        log.info("Received request to get timeline for orchestration: {} executionId: {}", orchName, executionId);
        List<TimelineEventDto> timeline = executionTimelineService.getTimeline(executionId);
        if (timeline == null || timeline.isEmpty()) {
            log.warn("No timeline events found for executionId: {}", executionId);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(timeline);
    }
}
