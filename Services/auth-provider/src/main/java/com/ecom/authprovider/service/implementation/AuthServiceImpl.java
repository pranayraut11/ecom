package com.ecom.authprovider.service.implementation;

import com.ecom.authprovider.dto.request.LoginRequest;
import com.ecom.authprovider.dto.request.LogoutRequest;
import com.ecom.authprovider.dto.response.LoginResponse;
import com.ecom.authprovider.exception.KeycloakServiceException;
import com.ecom.authprovider.service.specification.AuthService;
import com.ecom.authprovider.util.KeycloakUtil;
import com.ecom.shared.common.config.common.TenantContext;
import com.ecom.shared.common.config.common.TenantRequestBuilder;
import com.ecom.shared.common.config.httpclient.FilterableHttpClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ContentType;
import org.keycloak.TokenVerifier;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
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

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final KeycloakUtil keycloakUtil;
    private final ObjectMapper objectMapper;
    private static final String TOKEN_URL =
            "realms/{realm}/protocol/openid-connect/token";
    private final FilterableHttpClient httpClient;
    @Value("${keycloak.client-id}")
    private String clientId;
    @Value("${keycloak.client-secret}")
    private String clientSecret;
    /**
     * Authenticates a user with Keycloak and returns access tokens
     *
     * @param loginRequest the login credentials
     * @return the authentication response with tokens
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) throws JsonProcessingException {
        log.info("Authenticating user: {}", loginRequest.getUsername());
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost").port(8080)
                .path(TOKEN_URL)
                .buildAndExpand(TenantContext.getTenantId());
        Map<String, String> formData = new HashMap<>();
        formData.put("client_id", clientId); // your client id
        formData.put("username", loginRequest.getUsername());
        formData.put("password", loginRequest.getPassword());
        formData.put("grant_type", "password");
        formData.put("client_secret", clientSecret); // only if client is confidential
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriComponents.toUri())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(buildFormData(formData)))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request);
            return objectMapper.readValue(response.body(), LoginResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public static String buildFormData(Map<String, String> params) {
        return params.entrySet()
                .stream()
                .map(entry ->
                        URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" +
                                URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
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

