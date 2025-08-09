package com.ecom.authprovider.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO for user creation requests
 */
@Schema(description = "Request data for creating a new user")
public record UserCreateRequestDto(
    @Schema(description = "Username for the user", example = "john.doe")
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username,

    @Schema(description = "Password for the user", example = "Password123!")
    @NotBlank(message = "Password is required")
    String password,

    @Schema(description = "Email address", example = "john.doe@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @Schema(description = "First name", example = "John")
    String firstName,

    @Schema(description = "Last name", example = "Doe")
    String lastName,

    @Schema(description = "Whether the user account should be enabled", defaultValue = "true")
    Boolean enabled,

    @Schema(description = "Whether the email is already verified", defaultValue = "false")
    Boolean emailVerified,

    @Schema(description = "List of role names to assign to the user", example = "[\"user\", \"customer\"]")
    List<String> roles
) {
    // Constructor with default values for optional fields
    public UserCreateRequestDto {
        if (enabled == null) {
            enabled = true;
        }
        if (emailVerified == null) {
            emailVerified = false;
        }
        if (roles == null || roles.isEmpty()) {
            roles = List.of("user");
        }
    }
}
