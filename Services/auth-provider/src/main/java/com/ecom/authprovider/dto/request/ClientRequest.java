package com.ecom.authprovider.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientRequest {

    @NotBlank(message = "Client ID is required")
    private String clientId;

    private String redirectUri;

    private boolean publicClient = false;

    private String clientSecret;

    private boolean enabled = true;
}
