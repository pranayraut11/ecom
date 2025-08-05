package com.ecom.user.rest;

import com.ecom.shared.common.exception.EcomException;
import com.ecom.user.constant.enums.APIEndPoints;
import com.ecom.user.dto.AuthClientDetails;
import com.ecom.user.dto.KeycloakUser;
import com.ecom.user.dto.TokenDetails;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Objects;

import static com.ecom.user.constant.enums.AuthConstants.*;

@Component
@Slf4j
public class AuthRestService {

    private static final String AUTH_ERR_001 = "AUTH_ERR001";
    private final WebClient webClient;

    @Value("${auth-server.host}")
    private String host;

    AuthRestService(WebClient webClient) {
        this.webClient = webClient;
    }

    public TokenDetails login(AuthClientDetails authClientDetails, String realms) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.set(USERNAME, authClientDetails.getUsername());
        parameters.set(PASSWORD, authClientDetails.getPassword());
        parameters.set(CLIENT_ID, authClientDetails.getClientId());
        parameters.set(CLIENT_SECRET, authClientDetails.getClientSecret());
        parameters.set(GRANT_TYPE, PASSWORD);

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(host)
                .path(APIEndPoints.KEYCLOAK_TOKEN_URL)
                .buildAndExpand(realms);

        try {
            TokenDetails details = webClient.post()
                    .uri(uriComponents.toUriString())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(parameters))
                    .retrieve()
                    .onStatus(statusCode -> statusCode.is4xxClientError() || statusCode.is5xxServerError(), response -> {
                        log.error("Login failed with status code: {}", response.statusCode());
                        throw new EcomException(HttpStatus.INTERNAL_SERVER_ERROR, AUTH_ERR_001);
                    })
                    .bodyToMono(TokenDetails.class)
                    .block();

            if (Objects.nonNull(details)) {
//                AccessToken token = TokenVerifier.create(details.getAccess_token(), AccessToken.class).getToken();
//                if (Objects.nonNull(token.getRealmAccess())) {
//
//                    log.info("UserId {}", token.getSubject());
//                    details.setRoles(token.getRealmAccess().getRoles());
//                }
            }
            return details;
        } catch (Exception e) {
            log.error("Error during login", e);
            throw new EcomException(HttpStatus.INTERNAL_SERVER_ERROR, AUTH_ERR_001);
        }
    }

    public String createUser(KeycloakUser user, String realms, String token) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .host(host)
                .scheme("http")
                .port("8082")
                .path(APIEndPoints.KEYCLOAK_CREATE_USER_URL)
                .buildAndExpand(realms);

        try {
            // First create the user
            String userId  =  webClient.post()
                    .uri(uriComponents.toUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(user)
                    .retrieve()
                    .onStatus(status -> !status.equals(HttpStatus.CREATED), response -> {
                        log.error("Create user failed with status code: {}", response.statusCode());
                        throw new EcomException(HttpStatus.INTERNAL_SERVER_ERROR, AUTH_ERR_001);
                    })
                    .bodyToMono(String.class)
                    .block();

            log.info("User : {} created successfully ", user);
            return userId;
        } catch (Exception e) {
            log.error("Error creating user", e);
            throw new EcomException(HttpStatus.INTERNAL_SERVER_ERROR, AUTH_ERR_001);
        }
    }

    public void logout(String token, String realms) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(host)
                .path(APIEndPoints.KEYCLOAK_LOGOUT)
                .queryParam(CLIENT_ID, "user-service")
                .queryParam("access_token", token)
                .buildAndExpand(realms);

        try {
            webClient.post()
                    .uri(uriComponents.toUriString())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .retrieve()
                    .onStatus(statusCode -> statusCode.is4xxClientError() || statusCode.is5xxServerError(), response -> {
                        log.error("Logout failed with status code: {}", response.statusCode());
                        throw new EcomException(HttpStatus.INTERNAL_SERVER_ERROR, AUTH_ERR_001);
                    })
                    .bodyToMono(String.class)
                    .block();

            log.info("Successfully logged out from Keycloak");
        } catch (Exception e) {
            log.error("Error during logout", e);
            throw new EcomException(HttpStatus.INTERNAL_SERVER_ERROR, AUTH_ERR_001);
        }
    }
}
