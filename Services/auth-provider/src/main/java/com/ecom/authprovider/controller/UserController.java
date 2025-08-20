package com.ecom.authprovider.controller;

import com.ecom.authprovider.dto.request.LoginRequest;
import com.ecom.authprovider.dto.request.LogoutRequest;
import com.ecom.authprovider.dto.request.UserCreateRequestDto;
import com.ecom.authprovider.dto.response.ApiGenericResponse;
import com.ecom.authprovider.dto.response.LoginResponse;
import com.ecom.authprovider.dto.response.UserResponseDto;
import com.ecom.authprovider.service.specification.AuthService;
import com.ecom.authprovider.service.specification.UserService;
import com.ecom.shared.common.config.common.TenantContext;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    private final AuthService authService;

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
    public ResponseEntity<ApiGenericResponse<String>> createUser(
            @Valid @RequestBody UserCreateRequestDto requestDto) {

        log.info("Received request to create user '{}' ", requestDto.username());
        // Set the tenant context for this request
        String response = userService.createUser(requestDto);
            // Determine status code based on response
        log.info("User created successfully {}",response);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiGenericResponse.success("User created successfully",response));
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

    @PostMapping("login")
    @Operation(summary = "Authenticate user", description = "Authenticates a user with provided credentials and returns access tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed - invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Server error during authentication")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login request received for user: {}", loginRequest.getUsername());
        LoginResponse response = null;
        try {
            response = authService.login(loginRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("Login Success {}",response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Invalidates the user's refresh token to log them out")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request - missing refresh token"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid refresh token"),
            @ApiResponse(responseCode = "500", description = "Server error during logout")
    })
    public ResponseEntity<Void> logout(
            @Valid @RequestBody LogoutRequest logoutRequest) {

        log.info("Processing logout request for realm: {}", TenantContext.getTenantId());

        // Set tenant context for this request

        try {
            boolean logoutSuccessful = authService.logout(logoutRequest);

            if (logoutSuccessful) {
                log.info("Logout successful ");
                return ResponseEntity.ok().build();
            } else {
                log.warn("Logout failed");
                return ResponseEntity.badRequest().build();
            }
        } finally {
            // Clean up tenant context
            TenantContext.clear();
        }
    }
}
