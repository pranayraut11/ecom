package com.ecom.shared.authprovider.controller;

import com.ecom.shared.authprovider.dto.request.ClientRequest;
import com.ecom.shared.authprovider.dto.response.ApiGenericResponse;
import com.ecom.shared.authprovider.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth/realms/{realm}/clients")
@RequiredArgsConstructor
@Validated
@Tag(name = "Client Management", description = "APIs for managing Keycloak clients within realms")
public class ClientController {

    private final ClientService clientService;

    @Operation(
            summary = "Create a new client",
            description = "Creates a new client in the specified realm. Supports both public and confidential clients. If the client already exists, the operation succeeds without changes."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Client created or already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiGenericResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request or client creation failed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiGenericResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Realm not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiGenericResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<ApiGenericResponse<String>> createClient(
            @Parameter(description = "Name of the realm where the client will be created", required = true)
            @PathVariable("realm") String realm,
            @Valid @RequestBody ClientRequest request) {

        boolean created = clientService.createClient(realm, request);

        if (created) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiGenericResponse.success("Client created or already exists", request.getClientId()));
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiGenericResponse.error("Failed to create client"));
        }
    }
}
