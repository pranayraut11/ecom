package com.ecom.authprovider.service.implementation;

import com.ecom.authprovider.dto.request.LoginRequest;
import com.ecom.authprovider.dto.response.LoginResponse;
import com.ecom.authprovider.exception.KeycloakServiceException;
import com.ecom.authprovider.service.specification.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {



    /**
     * Authenticates a user with Keycloak and returns access tokens
     *
     * @param loginRequest the login credentials
     * @return the authentication response with tokens
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Authenticating user: {}", loginRequest.getUsername());

        Keycloak keycloakForAuthentication = null;
        try {
            // Create a direct user authentication client (not admin)
            keycloakForAuthentication = null;//KeycloakAdminConfig.getKeycloakClient();

            // Perform authentication
            AccessTokenResponse tokenResponse = keycloakForAuthentication.tokenManager().getAccessToken();

            // Build response
            return LoginResponse.builder()
                    .accessToken(tokenResponse.getToken())
                    .refreshToken(tokenResponse.getRefreshToken())
                    .tokenType(tokenResponse.getTokenType())
                    .expiresIn(tokenResponse.getExpiresIn())
                    .refreshExpiresIn(tokenResponse.getRefreshExpiresIn())
                    .build();
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", loginRequest.getUsername(), e);
            throw new KeycloakServiceException("Authentication failed: " + e.getMessage(), e);
        } finally {

        }
    }
}
