package com.ecom.authprovider.controller;

import com.ecom.authprovider.dto.request.RealmRequest;
import com.ecom.authprovider.dto.response.ApiGenericResponse;
import com.ecom.authprovider.service.RealmService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/auth/realms")
@RequiredArgsConstructor
@Validated
@Tag(name = "Realm Management", description = "APIs for managing Keycloak realms")
public class RealmController {

    private final RealmService realmService;

    @Operation(
        summary = "Create a new realm",
        description = "Creates a new realm in Keycloak with the specified configuration. If the realm already exists, the operation succeeds without changes."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Realm created or already exists",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiGenericResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request or realm creation failed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiGenericResponse.class))
        )
    })
    @PostMapping
    public ResponseEntity<ApiGenericResponse<String>> createRealm(@Valid @RequestBody RealmRequest request) {
        boolean created = realmService.createRealm(request);

        if (created) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiGenericResponse.success("Realm created or already exists", request.getName()));
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiGenericResponse.error("Failed to create realm"));
        }
    }

    @DeleteMapping("/{realm}")
    @Operation(
        summary = "Delete an existing realm",
        description = "Deletes the specified realm from Keycloak. If the realm does not exist, the operation succeeds without changes."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Realm deleted or did not exist",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiGenericResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request or realm deletion failed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiGenericResponse.class))
        )
    })
    public ResponseEntity<ApiGenericResponse<String>> deleteRealm(@PathVariable String realm) {
        boolean deleted = realmService.deleteRealm(realm);
        if (deleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiGenericResponse.success("Realm deleted or did not exist", realm));
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiGenericResponse.error("Failed to delete realm"));
        }
    }
}
