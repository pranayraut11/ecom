package com.ecom.authprovider.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for user response data
 */
@Schema(description = "Response data for user operations")
public record UserResponseDto(
    @Schema(description = "Unique identifier of the user")
    String id,

    @Schema(description = "Username of the user")
    String username,

    @Schema(description = "Email address of the user")
    String email,

    @Schema(description = "First name of the user")
    String firstName,

    @Schema(description = "Last name of the user")
    String lastName,

    @Schema(description = "Whether the user account is enabled")
    Boolean enabled,

    @Schema(description = "Whether the user's email is verified")
    Boolean emailVerified,

    @Schema(description = "Message describing the result of the operation")
    String message
) {
    /**
     * Creates a simple error response with a message
     *
     * @param errorMessage the error message
     * @return a UserResponseDto with only the error message
     */
    public static UserResponseDto error(String errorMessage) {
        return new UserResponseDto(
                null, null, null, null, null, null, null, errorMessage
        );
    }
}
