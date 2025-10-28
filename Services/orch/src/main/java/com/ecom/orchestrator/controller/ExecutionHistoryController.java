package com.ecom.orchestrator.controller;

import com.ecom.orchestrator.dto.ExecutionHistoryRequestDto;
import com.ecom.orchestrator.dto.PagedExecutionHistoryResponseDto;
import com.ecom.orchestrator.service.ExecutionHistoryService;
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
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Execution History", description = "APIs for retrieving orchestration execution history")
public class ExecutionHistoryController {

    private final ExecutionHistoryService executionHistoryService;

    @GetMapping("/orchestrations/{orchName}/executions")
    @Operation(
        summary = "Get execution history for a specific orchestration",
        description = "Retrieve paginated list of all executions for an orchestration with optional filtering by status and date range"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Execution history retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Orchestration not found")
    })
    public ResponseEntity<PagedExecutionHistoryResponseDto> getExecutionHistory(
            @Parameter(description = "Orchestration name", example = "tenantCreation", required = true)
            @PathVariable String orchName,

            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Field to sort by", example = "startTime")
            @RequestParam(required = false) String sortBy,

            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String direction,

            @Parameter(description = "Filter by execution status", example = "SUCCESS")
            @RequestParam(required = false) String status,

            @Parameter(description = "Filter by execution start date from (ISO format)", example = "2025-10-01T00:00:00")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,

            @Parameter(description = "Filter by execution start date to (ISO format)", example = "2025-10-31T23:59:59")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {

        try {
            log.info("Received request to get execution history for orchestration: {} - page: {}, size: {}",
                    orchName, page, size);

            // Create request DTO from individual parameters
            ExecutionHistoryRequestDto request = ExecutionHistoryRequestDto.builder()
                    .page(page)
                    .size(size)
                    .sortBy(sortBy)
                    .direction(direction)
                    .status(status)
                    .fromDate(fromDate)
                    .toDate(toDate)
                    .build();

            PagedExecutionHistoryResponseDto response = executionHistoryService.getExecutionHistory(orchName, request);

            if (response == null) {
                log.warn("Orchestration not found: {}", orchName);
                return ResponseEntity.notFound().build();
            }

            log.info("Successfully retrieved {} executions out of {} total for orchestration: {}",
                    response.getContent().size(), response.getTotalElements(), orchName);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error retrieving execution history for orchestration: {}", orchName, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
