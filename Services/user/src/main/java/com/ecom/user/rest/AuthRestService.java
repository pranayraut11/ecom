package com.ecom.user.rest;

import com.ecom.shared.common.config.httpclient.FilterableHttpClient;
import com.ecom.shared.common.exception.EcomException;
import com.ecom.shared.common.exception.ErrorResponse;
import com.ecom.user.constant.enums.APIEndPoints;
import com.ecom.user.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

import static com.ecom.user.constant.enums.AuthConstants.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthRestService {

    // Error codes
    private static final String AUTH_ERR_001 = "AUTH_ERR001";
    private static final String LOGIN_FAILED = "Authentication failed: Invalid credentials or service unavailable";
    private static final String USER_CREATION_FAILED = "User creation failed in authentication service";
    private static final String LOGOUT_FAILED = "Failed to logout user from authentication service";
    private static final String GENERIC_AUTH_ERROR = "Authentication service communication error";
    private static final String HTTP_STATUS_PREFIX = " (HTTP ";

    private final ObjectMapper objectMapper;
    private final FilterableHttpClient httpClient;

    @Value("${auth-server.host}")
    private String host;


    public TokenDetails login(Login authClientDetails) {
        try {
            String uri = "http://" + host + ":8082/" + APIEndPoints.KEYCLOAK_USER_LOGIN;

            // Convert request body to JSON
            String requestBody = objectMapper.writeValueAsString(authClientDetails);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request);

            int statusCode = response.statusCode();
            if (statusCode >= 400) {
                log.error("Login failed with status code: {}", statusCode);
                String errorMsg = LOGIN_FAILED + HTTP_STATUS_PREFIX + statusCode + ")";
                String responseBody = response.body();
                if (responseBody != null && !responseBody.isEmpty()) {
                    errorMsg = errorMsg + ": " + responseBody;
                }
                throw new EcomException(HttpStatus.INTERNAL_SERVER_ERROR, AUTH_ERR_001, errorMsg);
            }

            // Parse response into TokenDetails
            TokenDetails details = objectMapper.readValue(response.body(), TokenDetails.class);
            if (Objects.nonNull(details)) {
                log.debug("Login successful");
            }
            return details;

        } catch (Exception e) {
            log.error("Error during login", e);
            throw new EcomException(HttpStatus.INTERNAL_SERVER_ERROR, AUTH_ERR_001,
                    GENERIC_AUTH_ERROR + ": " + e.getMessage());
        }
    }
    public String createUser(KeycloakUser user) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .host(host)
                .scheme("http")
                .port("8082")
                .path(APIEndPoints.KEYCLOAK_CREATE_USER_URL)
                .build();
        String requestBody = null;
        try {
            requestBody = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriComponents.toUri())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        try {
            // First create the user and extract userId from Location header
            HttpResponse<String> response =  httpClient.send(request);
            return response.body();

        } catch (Exception e) {
            log.error("Error creating user", e);
            throw new EcomException(HttpStatus.INTERNAL_SERVER_ERROR, AUTH_ERR_001,
                    USER_CREATION_FAILED + ": " + e.getMessage());
        }
    }

    public void logout(String token, String realms) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(host)
                .path(APIEndPoints.KEYCLOAK_USER_LOGOUT)
                .queryParam("access_token", token)
                .buildAndExpand(realms);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriComponents.toUri())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            // First create the user and extract userId from Location header
            HttpResponse<String> response =  httpClient.send(request);

        } catch (Exception e) {
            log.error("Error creating user", e);
            throw new EcomException(HttpStatus.INTERNAL_SERVER_ERROR, AUTH_ERR_001,
                    USER_CREATION_FAILED + ": " + e.getMessage());
        }
    }
}
