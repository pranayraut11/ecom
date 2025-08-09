package com.ecom.authprovider.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing authentication tokens")
public class LoginResponse {

    @Schema(description = "Access token for authenticated requests")
    private String accessToken;

    @Schema(description = "Refresh token for obtaining a new access token")
    private String refreshToken;

    @Schema(description = "Token type, typically 'Bearer'")
    private String tokenType;

    @Schema(description = "Expiration time in seconds")
    private Long expiresIn;

    @Schema(description = "Refresh token expiration time in seconds")
    private Long refreshExpiresIn;
}
