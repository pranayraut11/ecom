package com.ecom.authprovider.service.implementation;

import com.ecom.authprovider.dto.request.LoginRequest;
import com.ecom.authprovider.dto.request.LogoutRequest;
import com.ecom.authprovider.dto.response.LoginResponse;
import com.ecom.authprovider.exception.KeycloakServiceException;
import com.ecom.authprovider.service.specification.AuthService;
import com.ecom.authprovider.util.KeycloakUtil;
import com.ecom.shared.common.config.common.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.TokenVerifier;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final KeycloakUtil keycloakUtil;

    /**
     * Authenticates a user with Keycloak and returns access tokens
     *
     * @param loginRequest the login credentials
     * @return the authentication response with tokens
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Authenticating user: {}", loginRequest.getUsername());

        try (Keycloak keycloakForAuthentication = keycloakUtil.createUserClient(TenantContext.getTenantId(),loginRequest.getUsername(),loginRequest.getPassword(),"admin-cli")) {

            try {
                // Create a direct user authentication client (not admin)
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

    @Override
    public boolean logout(LogoutRequest logoutRequest) {

        try (Keycloak keycloakForAuthentication = keycloakUtil.createUserClient(TenantContext.getTenantId(),"admin","admin","admin-cli")) {
            AccessToken accessToken = TokenVerifier.create(logoutRequest.getRefreshToken(), AccessToken.class).getToken();
            String userId = accessToken.getSubject();
            log.info("Logging out user with ID: {}", userId);
            keycloakForAuthentication.realm(TenantContext.getTenantId()).users().get(userId).logout();
            return true;
        } catch (VerificationException e) {
            throw new RuntimeException(e);
        }
    }
}

