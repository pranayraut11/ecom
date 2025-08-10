package com.ecom.authprovider.controller;

import com.ecom.authprovider.dto.request.RoleRequest;
import com.ecom.authprovider.dto.response.ApiGenericResponse;
import com.ecom.shared.common.config.common.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
class RoleControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private static final String TEST_REALM_NAME = "ecom";
    private static final String TEST_ROLE_NAME = "test-role";
    private static final String TEST_ROLE_DESCRIPTION = "Test Role Description";
    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/auth/realms/" + TEST_REALM_NAME + "/roles";

        // Set tenant ID in TenantContext for all tests
        TenantContext.setTenantId(TEST_REALM_NAME);

        // Create test realm if it doesn't exist (for test isolation)
        createTestRealmIfNotExists();
    }

    private void createTestRealmIfNotExists() {
        log.info("Ensuring test realm '{}' exists", TEST_REALM_NAME);
        String realmUrl = "http://localhost:" + port + "/auth/realms";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-ID", TEST_REALM_NAME);
        var realmRequest = new com.ecom.authprovider.dto.request.RealmRequest();
        realmRequest.setName(TEST_REALM_NAME);
        realmRequest.setEnabled(true);
        realmRequest.setDisplayName("Test Realm for Role API Tests");

        HttpEntity<com.ecom.authprovider.dto.request.RealmRequest> requestEntity =
            new HttpEntity<>(realmRequest, headers);

        try {
            restTemplate.exchange(
                realmUrl,
                HttpMethod.POST,
                requestEntity,
                ApiGenericResponse.class
            );
            log.info("Test realm created or already exists");
        } catch (Exception e) {
            log.error("Error creating test realm: {}", e.getMessage());
        }
    }

    @Test
    void shouldCreateRoleSuccessfully() {
        // Arrange
        RoleRequest request = new RoleRequest();
        request.setName(TEST_ROLE_NAME);
        request.setDescription(TEST_ROLE_DESCRIPTION);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-ID", TEST_REALM_NAME);
        HttpEntity<RoleRequest> requestEntity = new HttpEntity<>(request, headers);

        // Act
        ResponseEntity<ApiGenericResponse> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                requestEntity,
                ApiGenericResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).contains("created");
    }

    @Test
    void shouldHandleDuplicateRoleCreation() {
        // Arrange
        RoleRequest request = new RoleRequest();
        String roleName = TEST_ROLE_NAME + "-duplicate";
        request.setName(roleName);
        request.setDescription(TEST_ROLE_DESCRIPTION);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-ID", TEST_REALM_NAME);
        HttpEntity<RoleRequest> requestEntity = new HttpEntity<>(request, headers);

        // First call
        restTemplate.exchange(baseUrl, HttpMethod.POST, requestEntity, ApiGenericResponse.class);

        // Second call (should handle the duplicate)
        ResponseEntity<ApiGenericResponse> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                requestEntity,
                ApiGenericResponse.class);

        // Assert - expect successful response (409 or 200 depending on implementation)
        assertThat(response.getStatusCode().is2xxSuccessful() ||
                   response.getStatusCode().equals(HttpStatus.CONFLICT)).isTrue();
    }

    @Test
    void shouldReturnBadRequestForMissingRoleName() {
        // Arrange
        RoleRequest request = new RoleRequest();
        // No role name set
        request.setDescription(TEST_ROLE_DESCRIPTION);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-ID", TEST_REALM_NAME);
        HttpEntity<RoleRequest> requestEntity = new HttpEntity<>(request, headers);

        // Act
        ResponseEntity<ApiGenericResponse> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                requestEntity,
                ApiGenericResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldCreateRoleWithoutDescription() {
        // Arrange
        RoleRequest request = new RoleRequest();
        request.setName(TEST_ROLE_NAME + "-no-desc");
        // No description set

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-ID", TEST_REALM_NAME);
        HttpEntity<RoleRequest> requestEntity = new HttpEntity<>(request, headers);

        // Act
        ResponseEntity<ApiGenericResponse> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                requestEntity,
                ApiGenericResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void shouldHandleLongRoleName() {
        // Arrange
        RoleRequest request = new RoleRequest();
        request.setName("very-long-role-name-" + "x".repeat(50));
        request.setDescription(TEST_ROLE_DESCRIPTION);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-ID", TEST_REALM_NAME);
        HttpEntity<RoleRequest> requestEntity = new HttpEntity<>(request, headers);

        // Act
        ResponseEntity<ApiGenericResponse> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                requestEntity,
                ApiGenericResponse.class);

        // Assert - Either created successfully or rejected due to name length constraints
        assertThat(response.getStatusCode().is2xxSuccessful() ||
                  response.getStatusCode().equals(HttpStatus.BAD_REQUEST)).isTrue();
    }

    @Test
    void shouldHandleLongDescription() {
        // Arrange
        RoleRequest request = new RoleRequest();
        request.setName(TEST_ROLE_NAME + "-long-desc");
        request.setDescription("A".repeat(5000));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-ID", TEST_REALM_NAME);
        HttpEntity<RoleRequest> requestEntity = new HttpEntity<>(request, headers);

        // Act
        ResponseEntity<ApiGenericResponse> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                requestEntity,
                ApiGenericResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void shouldHandleSpecialCharactersInRoleName() {
        // Arrange
        RoleRequest request = new RoleRequest();
        request.setName("special_role-name.123");
        request.setDescription(TEST_ROLE_DESCRIPTION);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-ID", TEST_REALM_NAME);
        HttpEntity<RoleRequest> requestEntity = new HttpEntity<>(request, headers);

        // Act
        ResponseEntity<ApiGenericResponse> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                requestEntity,
                ApiGenericResponse.class);

        // Assert - Either created successfully or rejected due to invalid characters
        assertThat(response.getStatusCode().is2xxSuccessful() ||
                  response.getStatusCode().equals(HttpStatus.BAD_REQUEST)).isTrue();
    }

    @Test
    void shouldReturnBadRequestForEmptyRoleName() {
        // Arrange
        RoleRequest request = new RoleRequest();
        request.setName(""); // Empty name
        request.setDescription(TEST_ROLE_DESCRIPTION);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-ID", TEST_REALM_NAME);
        HttpEntity<RoleRequest> requestEntity = new HttpEntity<>(request, headers);

        // Act
        ResponseEntity<ApiGenericResponse> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                requestEntity,
                ApiGenericResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldHandleNonExistentRealm() {
        // Arrange
        String nonExistentRealmUrl = "http://localhost:" + port + "/auth/realms/non-existent-realm/roles";

        RoleRequest request = new RoleRequest();
        request.setName(TEST_ROLE_NAME + "-non-existent");
        request.setDescription(TEST_ROLE_DESCRIPTION);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-ID", TEST_REALM_NAME+"-non-existent");
        HttpEntity<RoleRequest> requestEntity = new HttpEntity<>(request, headers);

        // Act
        ResponseEntity<ApiGenericResponse> response = restTemplate.exchange(
                nonExistentRealmUrl,
                HttpMethod.POST,
                requestEntity,
                ApiGenericResponse.class);

        // Assert
        log.info("Status "+ response.getStatusCode().toString());
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }
}
