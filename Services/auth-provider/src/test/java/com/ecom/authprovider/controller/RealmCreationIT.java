package com.ecom.authprovider.controller;

import com.ecom.authprovider.dto.request.RealmRequest;
import com.ecom.authprovider.dto.response.RealmResponse;
import com.ecom.authprovider.dto.response.ApiGenericResponse;
import org.junit.jupiter.api.*;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.RealmRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the Realm creation API.
 * Uses Testcontainers to spin up a Keycloak instance.
 */
@SpringBootTest
@Import(ContainersConfig.class)
class RealmCreationIT {
    private static final Logger log = LoggerFactory.getLogger(RealmCreationIT.class);

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${keycloak.connection.server-url}")
    private String keycloakServerUrl;

    private Keycloak keycloakAdminClient;
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String MASTER_REALM = "master";
    private static final String TEST_REALM_NAME = "demoRealm";
    private static final String TEST_REALM_DESCRIPTION = "Test realm for demo";


    @BeforeEach
    void setUp() {
        String keycloakUrl = keycloakServerUrl;
        log.info("Setting up Keycloak admin client with URL: {}", keycloakUrl);

        keycloakAdminClient = KeycloakBuilder.builder()
                .serverUrl(keycloakUrl)
                .realm(MASTER_REALM)
                .username(ADMIN_USERNAME)
                .password(ADMIN_PASSWORD)
                .clientId("admin-cli")
                .grantType(OAuth2Constants.PASSWORD)
                .build();

        // Clean up any existing test realm before each test
        cleanupTestRealm();
    }

    @AfterEach
    void tearDown() {
        cleanupTestRealm();
    }

    private void cleanupTestRealm() {
        try {
            log.info("Cleaning up test realm: {}", TEST_REALM_NAME);
            List<RealmRepresentation> realms = keycloakAdminClient.realms().findAll();
            realms.stream()
                  .filter(realm -> realm.getRealm().equals(TEST_REALM_NAME))
                  .forEach(realm -> {
                      log.info("Removing realm: {}", realm.getRealm());
                      keycloakAdminClient.realm(realm.getRealm()).remove();
                  });
        } catch (Exception e) {
            log.warn("Error during test realm cleanup: {}", e.getMessage());
        }
    }

    @Test
    void shouldCreateRealmSuccessfully() {
        // Arrange
        RealmRequest request = new RealmRequest();
        request.setName(TEST_REALM_NAME);
        request.setEnabled(true);
        request.setDisplayName(TEST_REALM_DESCRIPTION);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RealmRequest> requestEntity = new HttpEntity<>(request, headers);
        String url = "http://localhost:" + port + "/auth/realms";

        log.info("Sending request to create realm: {}", TEST_REALM_NAME);

        // Act
        ResponseEntity<RealmResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                RealmResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(TEST_REALM_NAME);
        assertThat(response.getBody().getDisplayName()).isEqualTo(TEST_REALM_DESCRIPTION);
        assertThat(response.getBody().getId()).isNotEmpty();

        // Verify realm was created in Keycloak
        RealmRepresentation createdRealm = keycloakAdminClient.realm(TEST_REALM_NAME).toRepresentation();
        assertThat(createdRealm).isNotNull();
        assertThat(createdRealm.getRealm()).isEqualTo(TEST_REALM_NAME);
        assertThat(createdRealm.isEnabled()).isTrue();
        assertThat(createdRealm.getDisplayName()).isEqualTo(TEST_REALM_DESCRIPTION);

        log.info("Successfully verified realm creation: {}", TEST_REALM_NAME);
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

        log.info("First call - Creating realm: {}", TEST_REALM_NAME);

        // Act - First call should succeed
        ResponseEntity<RealmResponse> firstResponse = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                RealmResponse.class);

        // Assert first call success
        assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        log.info("Second call - Attempting to create duplicate realm: {}", TEST_REALM_NAME);

        // Act - Second call should fail with conflict
        ResponseEntity<ApiGenericResponse> secondResponse = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                ApiGenericResponse.class);

        // Assert second call failure
        assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(secondResponse.getBody()).isNotNull();
        assertThat(secondResponse.getBody().getMessage()).contains(TEST_REALM_NAME);

        // Verify only one realm exists with that name
        List<RealmRepresentation> realms = keycloakAdminClient.realms().findAll();
        long count = realms.stream()
                .filter(realm -> realm.getRealm().equals(TEST_REALM_NAME))
                .count();
        assertThat(count).isEqualTo(1);

        log.info("Successfully verified conflict on duplicate realm creation");
    }
}
