package com.ecom.orchestrator.controller;

import com.ecom.orchestrator.dto.OrchestrationDetailsResponseDto;
import com.ecom.orchestrator.service.OrchestrationDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Orchestration Details", description = "APIs for retrieving detailed orchestration registration information")
public class OrchestrationDetailsController {

    private final OrchestrationDetailsService orchestrationDetailsService;

    @GetMapping("/orchestrations/{orchName}")
    @Operation(
        summary = "Get detailed registration information for a specific orchestration",
        description = "Retrieve comprehensive details about an orchestration including all steps, their registration status, and registered workers"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orchestration details retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Orchestration not found")
    })
    public ResponseEntity<OrchestrationDetailsResponseDto> getOrchestrationDetails(
            @Parameter(description = "Orchestration name", example = "tenantCreation", required = true)
            @PathVariable String orchName) {

        try {
            log.info("Received request to get orchestration details for: {}", orchName);

            OrchestrationDetailsResponseDto details = orchestrationDetailsService.getOrchestrationDetails(orchName);

            if (details == null) {
                log.warn("Orchestration not found: {}", orchName);
                return ResponseEntity.notFound().build();
            }

            log.info("Successfully retrieved details for orchestration: {} with {} steps",
                    orchName, details.getSteps().size());

            return ResponseEntity.ok(details);

        } catch (Exception e) {
            log.error("Error retrieving orchestration details for: {}", orchName, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
