package com.ecom.authprovider.controller;

import com.ecom.authprovider.dto.request.UserCreateRequestDto;
import com.ecom.authprovider.dto.response.UserResponseDto;
import com.ecom.authprovider.service.specification.UserService;
import com.ecom.shared.common.config.common.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user management operations
 */
@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "APIs for managing users in Keycloak")
public class UserController {

    private final UserService userService;

    /**
     * Creates a new user
     *
     * @param requestDto the user creation request data
     * @return response containing the created user details or error information
     */
    @PostMapping()
    @Operation(summary = "Create a new user", description = "Creates a new user in the specified realm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or user already exists"),
            @ApiResponse(responseCode = "404", description = "Realm not found"),
            @ApiResponse(responseCode = "500", description = "Server error during user creation")
    })
    public ResponseEntity<UserResponseDto> createUser(
            @Valid @RequestBody UserCreateRequestDto requestDto) {

        log.info("Received request to create user '{}' ", requestDto.username());

        // Set the tenant context for this request

        try {
            UserResponseDto response = userService.createUser(requestDto);

            // Determine status code based on response
            if (response.id() != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                // Check if message indicates an existing user
                if (response.message() != null && response.message().contains("already exists")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
                // Check if message indicates realm not found
                else if (response.message() != null && response.message().contains("Realm not found")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
                // Default to internal server error for other issues
                else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
            }
        } finally {

        }
    }

    /**
     * Retrieves a user by ID
     *
     * @param userId    the ID of the user to retrieve
     * @param realmName the realm name from path variable
     * @return response containing the user details or error information
     */
    @GetMapping("/{realmName}/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID from the specified realm")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User or realm not found"),
            @ApiResponse(responseCode = "500", description = "Server error during user retrieval")
    })
    public ResponseEntity<UserResponseDto> getUserById(
            @Parameter(description = "The user ID", required = true)
            @PathVariable String userId,
            @Parameter(description = "The realm name", required = true)
            @PathVariable String realmName) {

        log.info("Received request to get user with ID '{}' from realm '{}'", userId, realmName);

        // Set the tenant context for this request
        TenantContext.setTenantId(realmName);

        try {
            UserResponseDto response = userService.getUserById(userId);

            // Determine status code based on response
            if (response.id() != null) {
                return ResponseEntity.ok(response);
            } else {
                // Check if message indicates user not found
                if (response.message() != null && response.message().contains("not found")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
                // Default to internal server error for other issues
                else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
            }
        } finally {
            // Clean up the tenant context
            TenantContext.clear();
        }
    }
}
