package com.ecom.authprovider.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for user logout")
public class LogoutRequest {

    @NotBlank(message = "Refresh token is required")
    @Schema(description = "Refresh token to invalidate", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "Client ID (optional, defaults to configured client)", example = "frontend-app")
    private String clientId;
}
