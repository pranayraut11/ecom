package com.ecom.user.rest;

import com.ecom.shared.common.exception.EcomException;
import com.ecom.user.constant.enums.APIEndPoints;
import com.ecom.user.dto.AuthClientDetails;
import com.ecom.user.dto.KeycloakUser;
import com.ecom.user.dto.TokenDetails;
import com.ecom.user.dto.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

import static com.ecom.user.constant.enums.AuthConstants.*;
import static org.springframework.security.config.Elements.HTTP;

@Component
@Slf4j
public class KeycloakAuthService {

    private RestTemplate restTemplate;
    private ObjectMapper mapper;

    @Value("${user.auth.server.host}")
    private String host;



    KeycloakAuthService(RestTemplate restTemplate, ObjectMapper mapper) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
    }

    public TokenDetails login(AuthClientDetails authClientDetails, String realms) throws VerificationException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap parameters = new LinkedMultiValueMap<String, String>();
        parameters.set(USERNAME,authClientDetails.getUsername());
        parameters.set(PASSWORD,authClientDetails.getPassword());
        parameters.set(CLIENT_ID,authClientDetails.getClientId());
        parameters.set(CLIENT_SECRET,authClientDetails.getClientSecret());
        parameters.set(GRANT_TYPE,PASSWORD);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(parameters, headers);
        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme(HTTP).host(host).path(APIEndPoints.KEYCLOAK_TOKEN_URL).buildAndExpand(realms);
        ResponseEntity<TokenDetails> tokenDetails = null;
            tokenDetails = restTemplate.postForEntity(uriComponents.toUriString(), entity, TokenDetails.class);

        if (Objects.nonNull(tokenDetails) && tokenDetails.getStatusCode().is2xxSuccessful()) {
            TokenDetails details = tokenDetails.getBody();
            AccessToken token = TokenVerifier.create(details.getAccess_token(), AccessToken.class).getToken();
            if(Objects.nonNull(token.getRealmAccess())) {

                log.info("UserId {}",token.getSubject());
                details.setRoles(token.getRealmAccess().getRoles());
            }
            return details;
        } else {
            throw new EcomException(tokenDetails.getStatusCode(),"AUTH_ERR001","message",false);
        }
    }

    public void createUser(KeycloakUser user, String realms, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<User> entity = new HttpEntity<>(user,headers);
        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme(HTTP).host(host).path(APIEndPoints.KEYCLOAK_CREATE_USER_URL).buildAndExpand(realms);
        ResponseEntity<String> tokenDetails = null;
        tokenDetails = restTemplate.postForEntity(uriComponents.toUriString(), entity, String.class);

        if (Objects.nonNull(tokenDetails) && tokenDetails.getStatusCode().equals(HttpStatus.CREATED)) {
            log.info("User : {} created successfully ",user);
        } else {
            throw new EcomException(tokenDetails.getStatusCode(),"AUTH_ERR001","message",false);
        }
    }

    public void logout(String token, String realms) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);
        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme(HTTP).host(host).path(APIEndPoints.KEYCLOAK_LOGOUT).queryParam(CLIENT_ID, "user-service").queryParam("access_token", token).buildAndExpand(realms);
        ResponseEntity<String> logoutResponse = null;
        logoutResponse = restTemplate.postForEntity(uriComponents.toUriString(),entity, String.class);

        if (Objects.nonNull(logoutResponse) && logoutResponse.getStatusCode().is2xxSuccessful()) {
            log.info("Successfulley logged out from Keycloak");
        } else {
            throw new EcomException(logoutResponse.getStatusCode(),"AUTH_ERR001","message",false);
        }
    }

}
