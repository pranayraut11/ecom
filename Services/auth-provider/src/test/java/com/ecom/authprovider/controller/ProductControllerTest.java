package com.ecom.authprovider.controller;

import com.ecom.authprovider.dto.request.RealmRequest;
import com.ecom.authprovider.dto.response.ApiGenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@Import(ContainersConfig.class)
class ProductControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;

    private static final String TEST_REALM_NAME = "demoRealm";
    private static final String TEST_REALM_DESCRIPTION = "Test realm for demo";

    @BeforeAll
    static void setup() {
        log.info("Setting up ProductControllerTest...");
    }

    @Test
    void shouldCreateRealmSuccessfully() {
        // Arrange
        RealmRequest request = new RealmRequest();
        request.setName(TEST_REALM_NAME+ "1"); // Ensure unique name for each test run
        request.setEnabled(true);
        request.setDisplayName(TEST_REALM_DESCRIPTION);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RealmRequest> requestEntity = new HttpEntity<>(request, headers);
        String url = "http://localhost:" + port + "/auth/realms";

        log.info("Sending request to create realm: {}", TEST_REALM_NAME);

        // Act
        ResponseEntity<ApiGenericResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                ApiGenericResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldReturnConflictWhenRealmAlreadyExists() {
        // Arrange
        RealmRequest request = new RealmRequest();
        request.setName(TEST_REALM_NAME);
        request.setEnabled(true);
        request.setDisplayName(TEST_REALM_DESCRIPTION);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RealmRequest> requestEntity = new HttpEntity<>(request, headers);
        String url = "http://localhost:" + port + "/auth/realms";
        // First call
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, ApiGenericResponse.class);
        // Second call (should fail)
        ResponseEntity<ApiGenericResponse> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ApiGenericResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturnBadRequestForMissingName() {
        RealmRequest request = new RealmRequest();
        request.setEnabled(true);
        request.setDisplayName(TEST_REALM_DESCRIPTION);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RealmRequest> requestEntity = new HttpEntity<>(request, headers);
        String url = "http://localhost:" + port + "/auth/realms";
        ResponseEntity<ApiGenericResponse> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ApiGenericResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturnBadRequestForInvalidRealmName() {
        RealmRequest request = new RealmRequest();
        request.setName(""); // Invalid name
        request.setEnabled(true);
        request.setDisplayName(TEST_REALM_DESCRIPTION);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RealmRequest> requestEntity = new HttpEntity<>(request, headers);
        String url = "http://localhost:" + port + "/auth/realms";
        ResponseEntity<ApiGenericResponse> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ApiGenericResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldCreateDisabledRealm() {
        RealmRequest request = new RealmRequest();
        request.setName("disabledRealm");
        request.setEnabled(false);
        request.setDisplayName("Disabled realm");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RealmRequest> requestEntity = new HttpEntity<>(request, headers);
        String url = "http://localhost:" + port + "/auth/realms";
        ResponseEntity<ApiGenericResponse> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ApiGenericResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void shouldHandleLargeDescription() {
        RealmRequest request = new RealmRequest();
        request.setName("largeDescRealm");
        request.setEnabled(true);
        request.setDisplayName("A".repeat(5000));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RealmRequest> requestEntity = new HttpEntity<>(request, headers);
        String url = "http://localhost:" + port + "/auth/realms";
        ResponseEntity<ApiGenericResponse> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ApiGenericResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void shouldReturnBadRequestForNullBody() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        String url = "http://localhost:" + port + "/auth/realms";
        ResponseEntity<ApiGenericResponse> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ApiGenericResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
