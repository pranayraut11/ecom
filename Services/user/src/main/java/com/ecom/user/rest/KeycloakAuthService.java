package com.ecom.user.rest;

import com.ecom.user.constant.enums.APIEndPoints;
import com.ecom.user.constant.enums.AuthConstants;
import com.ecom.user.constant.enums.Function;
import com.ecom.user.dto.AuthClientDetails;
import com.ecom.user.dto.TokenDetails;
import com.ecom.user.exception.handler.EcomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

import static com.ecom.user.constant.enums.AuthConstants.*;

@Component
public class KeycloakAuthService {

    private RestTemplate restTemplate;
    private ObjectMapper mapper;

    @Value("${user.auth.server.host}")
    private String host;



    KeycloakAuthService(RestTemplate restTemplate, ObjectMapper mapper) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
    }

    public TokenDetails login(AuthClientDetails authClientDetails,String realms) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(authClientDetails.getClient_id(),authClientDetails.getClient_secret(), null);
        MultiValueMap parameters = new LinkedMultiValueMap<String, String>();
        parameters.set(USERNAME,authClientDetails.getUsername());
        parameters.set(PASSWORD,authClientDetails.getPassword());
        parameters.set(GRANT_TYPE,PASSWORD);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(parameters, headers);
        UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme(HTTP).host(host).path(APIEndPoints.KEYCLOAK_TOKEN_URL).buildAndExpand(realms);
        ResponseEntity<TokenDetails> tokenDetails = null;
            tokenDetails = restTemplate.postForEntity(uriComponents.toUriString(), entity, TokenDetails.class);

        if (Objects.nonNull(tokenDetails) && tokenDetails.getStatusCode().is2xxSuccessful()) {
            return tokenDetails.getBody();
        } else {
            throw new EcomException(Function.AUTHENTICATION, tokenDetails.getStatusCode());
        }
    }
}
