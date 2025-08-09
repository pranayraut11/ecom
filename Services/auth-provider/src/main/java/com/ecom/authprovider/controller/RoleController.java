package com.ecom.authprovider.controller;

import com.ecom.authprovider.dto.request.RoleRequest;
import com.ecom.authprovider.dto.response.ApiGenericResponse;
import com.ecom.authprovider.service.RoleService;
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
@RequestMapping("/auth/realms/{realm}/roles")
@RequiredArgsConstructor
@Validated
@Tag(name = "Role Management", description = "APIs for managing Keycloak roles within realms")
public class RoleController {

    private final RoleService roleService;

    @Operation(
        summary = "Create a new role",
        description = "Creates a new role in the specified realm. If the role already exists, the operation succeeds without changes."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Role created or already exists",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiGenericResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request or role creation failed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiGenericResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Realm not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiGenericResponse.class))
        )
    })
    @PostMapping
    public ResponseEntity<ApiGenericResponse<String>> createRole(
            @Parameter(description = "Name of the realm where the role will be created", required = true)
            @Valid @RequestBody RoleRequest request) {

        boolean created = roleService.createRole(request);

        if (created) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiGenericResponse.success("Role created or already exists", request.getName()));
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiGenericResponse.error("Failed to create role"));
        }
    }
}
