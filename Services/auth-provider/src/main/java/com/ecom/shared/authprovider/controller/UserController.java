package com.ecom.shared.authprovider.controller;

import com.ecom.shared.authprovider.dto.request.UserRequest;
import com.ecom.shared.authprovider.dto.response.ApiGenericResponse;
import com.ecom.shared.authprovider.service.UserService;
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
@RequestMapping("/auth/realms/{realm}/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Management", description = "APIs for managing Keycloak users within realms")
public class UserController {

    private final UserService userService;

    @Operation(
        summary = "Create a new user",
        description = "Creates a new user in the specified realm and assigns roles if provided. If the user already exists, the operation updates the user's roles."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "User created or already exists",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiGenericResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request or user creation failed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiGenericResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Realm not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiGenericResponse.class))
        )
    })
    @PostMapping
    public ResponseEntity<ApiGenericResponse<String>> createUser(
            @Parameter(description = "Name of the realm where the user will be created", required = true)
            @PathVariable("realm") String realm,
            @Valid @RequestBody UserRequest request) {

        String userId = userService.createUser(realm, request);

        if (userId != null) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiGenericResponse.success("User created or already exists", userId));
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiGenericResponse.error("Failed to create user"));
        }
    }
}
