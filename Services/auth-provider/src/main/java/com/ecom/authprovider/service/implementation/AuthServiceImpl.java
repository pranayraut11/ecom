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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final KeycloakUtil keycloakUtil;
    private final WebClient webClient;
    private static final String TOKEN_URL =
            "realms/demo22/protocol/openid-connect/token";
    /**
     * Authenticates a user with Keycloak and returns access tokens
     *
     * @param loginRequest the login credentials
     * @return the authentication response with tokens
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Authenticating user: {}", loginRequest.getUsername());
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost").port(8080)
                .path(TOKEN_URL)
                .build();
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", "user-app"); // your client id
        formData.add("username", loginRequest.getUsername());
        formData.add("password", loginRequest.getPassword());
        formData.add("grant_type", "password");
        formData.add("client_secret", "J5gGIiYRq2EcH0cqNiWCdz5HS7UCpOLJ"); // only if client is confidential

        return webClient.post().uri(uriComponents.toUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(LoginResponse.class)   // or a DTO (see below)
                .block(); // contains access_token, refresh_token

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
            throw new KeycloakServiceException(e.getMessage());
        }
    }
}

