package com.ecom.user.rest;

import com.ecom.shared.common.exception.EcomException;
import com.ecom.shared.common.exception.ErrorResponse;
import com.ecom.user.constant.enums.APIEndPoints;
import com.ecom.user.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.ecom.user.constant.enums.AuthConstants.*;

@Component
@Slf4j
public class AuthRestService {

    // Error codes
    private static final String AUTH_ERR_001 = "AUTH_ERR001";
    private static final String LOGIN_FAILED = "Authentication failed: Invalid credentials or service unavailable";
    private static final String USER_CREATION_FAILED = "User creation failed in authentication service";
    private static final String LOGOUT_FAILED = "Failed to logout user from authentication service";
    private static final String GENERIC_AUTH_ERROR = "Authentication service communication error";
    private static final String HTTP_STATUS_PREFIX = " (HTTP ";

    private final WebClient webClient;

    @Value("${auth-server.host}")
    private String host;

    AuthRestService(WebClient webClient) {
        this.webClient = webClient;
    }

    public TokenDetails login(Login authClientDetails) {

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(host).port(8082)
                .path(APIEndPoints.KEYCLOAK_USER_LOGIN)
                .build();

        try {
            TokenDetails details = webClient.post()
                    .uri(uriComponents.toUriString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(authClientDetails)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                        log.error("Login failed with status code: {}", response.statusCode());
                        final String errorMsg = LOGIN_FAILED + HTTP_STATUS_PREFIX + response.statusCode().value() + ")";
                        return response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    String fullErrorMsg = errorMsg;
                                    if (!body.isEmpty()) {
                                        fullErrorMsg = fullErrorMsg + ": " + body;
                                    }
                                    return reactor.core.publisher.Mono.error(
                                            new EcomException(HttpStatus.INTERNAL_SERVER_ERROR, AUTH_ERR_001, fullErrorMsg));
                                });
                    })
                    .bodyToMono(TokenDetails.class)
                    .block();

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

        try {
            // First create the user and extract userId from Location header
            return webClient.post()
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(user)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                        log.error("Create user failed with status code: {}", response.statusCode());
                        return response.bodyToMono(ErrorResponse.class)
                                .flatMap(body -> {
                                    String errorMessage =  body.getMessage();

                                    log.error("Error response: {}", body);
                                    return Mono.error(
                                            new EcomException(HttpStatus.INTERNAL_SERVER_ERROR, AUTH_ERR_001, errorMessage));
                                });
                    })
                    .bodyToMono(ApiGenericResponse.class)
                    .map(response -> {
                        Object locationHeader = response.getData();
                        if (locationHeader != null) {
                            // Extract user ID from location header which typically looks like "/users/{userId}"
                           // String userId = locationHeader.substring(locationHeader.lastIndexOf('/') + 1);
                            //log.info("User created successfully with ID: {}", userId);
                            return locationHeader.toString();
                        }
                        log.warn("User created but no Location header returned");
                        throw new EcomException(HttpStatus.INTERNAL_SERVER_ERROR, AUTH_ERR_001,
                                "User created but couldn't extract user ID from response");
                    })
                    .block();
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

        try {
            webClient.post()
                    .uri(uriComponents.toUriString())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                        log.error("Logout failed with status code: {}", response.statusCode());
                        return response.bodyToMono(ErrorResponse.class)
                                .defaultIfEmpty(null)
                                .flatMap(body -> {
                                    String errorMessage = LOGOUT_FAILED +
                                            HTTP_STATUS_PREFIX + response.statusCode().value() + ")";

                                    return reactor.core.publisher.Mono.error(
                                            new EcomException(HttpStatus.INTERNAL_SERVER_ERROR, AUTH_ERR_001, errorMessage));
                                });
                    })
                    .bodyToMono(String.class)
                    .block();

            log.info("Successfully logged out from Keycloak");
        } catch (Exception e) {
            log.error("Error during logout", e);
            throw new EcomException(HttpStatus.INTERNAL_SERVER_ERROR, AUTH_ERR_001,
                    LOGOUT_FAILED + ": " + e.getMessage());
        }
    }
}
