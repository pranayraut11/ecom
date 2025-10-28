package com.ecom.orchestrator.controller;

import com.ecom.orchestrator.dto.OrchestrationListRequestDto;
import com.ecom.orchestrator.dto.PagedOrchestrationResponseDto;
import com.ecom.orchestrator.service.OrchestrationListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ecom.orchestrator.exception.OrchestrationListException;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Orchestration List", description = "APIs for listing and querying registered orchestrations")
public class OrchestrationListController {

    private final OrchestrationListService orchestrationListService;

    @GetMapping("/orchestrations")
    @Operation(
        summary = "List all registered orchestrations",
        description = "Retrieve a paginated list of orchestrations with optional filtering and sorting"
    )
    public ResponseEntity<PagedOrchestrationResponseDto> listOrchestrations(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Field to sort by", example = "orchName")
            @RequestParam(required = false) String sortBy,

            @Parameter(description = "Sort direction (asc/desc)", example = "asc")
            @RequestParam(defaultValue = "desc") String direction,

            @Parameter(description = "Filter by status", example = "REGISTERED")
            @RequestParam(required = false) String status,

            @Parameter(description = "Filter by type", example = "SEQUENTIAL")
            @RequestParam(required = false) String type,

            @Parameter(description = "Filter by orchestration name (case-insensitive contains)", example = "tenant")
            @RequestParam(required = false) String orchName,

            @Parameter(description = "Filter by registration date from (ISO format)", example = "2025-10-01T00:00:00")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime registeredFrom,

            @Parameter(description = "Filter by registration date to (ISO format)", example = "2025-10-31T23:59:59")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime registeredTo) {

        try {
            log.info("Received request to list orchestrations - page: {}, size: {}, sortBy: {}, direction: {}",
                    page, size, sortBy, direction);

            // Create request DTO from individual parameters
            OrchestrationListRequestDto request = OrchestrationListRequestDto.builder()
                    .page(page)
                    .size(size)
                    .sortBy(sortBy)
                    .direction(direction)
                    .status(status)
                    .type(type)
                    .orchName(orchName)
                    .registeredFrom(registeredFrom)
                    .registeredTo(registeredTo)
                    .build();

            PagedOrchestrationResponseDto response = orchestrationListService.listOrchestrations(request);

            log.info("Successfully retrieved {} orchestrations out of {} total",
                    response.getContent().size(), response.getTotalElements());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error listing orchestrations", e);
            throw new OrchestrationListException("Failed to retrieve orchestrations: " + e.getMessage(), e);
        }
    }
}
