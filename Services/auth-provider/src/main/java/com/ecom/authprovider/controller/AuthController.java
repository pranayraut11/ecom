package com.ecom.authprovider.controller;

import com.ecom.authprovider.dto.request.LoginRequest;
import com.ecom.authprovider.dto.request.LogoutRequest;
import com.ecom.authprovider.dto.response.LoginResponse;
import com.ecom.authprovider.service.specification.AuthService;
import com.ecom.shared.common.config.common.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "APIs for authentication operations")
public class AuthController {

    private final AuthService authService;

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
        LoginResponse response = authService.login(loginRequest);
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
