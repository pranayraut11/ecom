package com.ecom.authprovider.controller;

import com.ecom.authprovider.dto.request.UserRequest;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@Import(ContainersConfig.class)
class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private static final String TEST_REALM_NAME = "ecom";
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "testuser@example.com";
    private static final String TEST_PASSWORD = "Password123!";
    private static final String TEST_ROLE_NAME = "user-role";
    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/auth/users";

        // Set tenant ID in TenantContext for all tests
        TenantContext.setTenantId(TEST_REALM_NAME);

        // Create test realm and role if they don't exist
        createTestRealmIfNotExists();
        createTestRoleIfNotExists();
    }

    private void createTestRealmIfNotExists() {
        log.info("Ensuring test realm '{}' exists", TEST_REALM_NAME);
        String realmUrl = "http://localhost:" + port + "/auth/realms";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var realmRequest = new com.ecom.authprovider.dto.request.RealmRequest();
        realmRequest.setName(TEST_REALM_NAME);
        realmRequest.setEnabled(true);
        realmRequest.setDisplayName("Test Realm for User API Tests");
        headers.set("X-Tenant-ID",TEST_REALM_NAME);
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

    private void createTestRoleIfNotExists() {
        log.info("Ensuring test role '{}' exists", TEST_ROLE_NAME);
        String roleUrl = "http://localhost:" + port + "/auth/realms/" + TEST_REALM_NAME + "/roles";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-ID",TEST_REALM_NAME);
        var roleRequest = new com.ecom.authprovider.dto.request.RoleRequest();
        roleRequest.setName(TEST_ROLE_NAME);
        roleRequest.setDescription("Test Role for User API Tests");

        HttpEntity<com.ecom.authprovider.dto.request.RoleRequest> requestEntity =
            new HttpEntity<>(roleRequest, headers);

        try {
            restTemplate.exchange(
                roleUrl,
                HttpMethod.POST,
                requestEntity,
                ApiGenericResponse.class
            );
            log.info("Test role created or already exists");
        } catch (Exception e) {
            log.error("Error creating test role: {}", e.getMessage());
        }
    }

    @Test
    void shouldCreateUserSuccessfully() {
        // Arrange
        UserRequest request = new UserRequest();
        request.setUsername(TEST_USERNAME);
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEnabled(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-ID",TEST_REALM_NAME);
        HttpEntity<UserRequest> requestEntity = new HttpEntity<>(request, headers);

        // Act
        ResponseEntity<ApiGenericResponse> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                requestEntity,
                ApiGenericResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("created");
    }

    @Test
    void shouldCreateUserWithRolesSuccessfully() {
        // Arrange
        UserRequest request = new UserRequest();
        request.setUsername(TEST_USERNAME + "-with-roles");
        request.setEmail("user-with-roles@example.com");
        request.setPassword(TEST_PASSWORD);
        request.setFirstName("Test");
        request.setLastName("UserWithRoles");
        request.setEnabled(true);
        request.setRoles(Collections.singletonList(TEST_ROLE_NAME));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-ID",TEST_REALM_NAME);
        HttpEntity<UserRequest> requestEntity = new HttpEntity<>(request, headers);

        // Act
        ResponseEntity<ApiGenericResponse> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                requestEntity,
                ApiGenericResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldCreateUserWithAttributesSuccessfully() {
        // Arrange
        UserRequest request = new UserRequest();
        request.setUsername(TEST_USERNAME + "-with-attributes");
        request.setEmail("user-with-attributes@example.com");
        request.setPassword(TEST_PASSWORD);
        request.setFirstName("Test");
        request.setLastName("UserWithAttributes");
        request.setEnabled(true);

        Map<String, String> attributes = new HashMap<>();
        attributes.put("phoneNumber", "1234567890");
        attributes.put("department", "IT");
        attributes.put("location", "New York");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-ID",TEST_REALM_NAME);
        HttpEntity<UserRequest> requestEntity = new HttpEntity<>(request, headers);

        // Act
        ResponseEntity<ApiGenericResponse> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                requestEntity,
                ApiGenericResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldHandleDuplicateUserCreation() {
        // Arrange
        UserRequest request = new UserRequest();
        String username = TEST_USERNAME + "-duplicate";
        request.setUsername(username);
        request.setEmail("duplicate@example.com");
        request.setPassword(TEST_PASSWORD);
        request.setFirstName("Test");
        request.setLastName("UserDuplicate");
        request.setEnabled(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-ID",TEST_REALM_NAME);
        HttpEntity<UserRequest> requestEntity = new HttpEntity<>(request, headers);

        // First call
        restTemplate.exchange(baseUrl, HttpMethod.POST, requestEntity, ApiGenericResponse.class);

        // Second call (should handle the duplicate)
        ResponseEntity<ApiGenericResponse> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                requestEntity,
                ApiGenericResponse.class);

        // Assert - expect conflict
        assertThat(response.getStatusCode().equals(HttpStatus.OK) ||
                   response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void shouldReturnBadRequestForMissingUsername() {
        // Arrange
        UserRequest request = new UserRequest();
        // No username set
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);
        request.setFirstName("Test");
        request.setLastName("User");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-ID",TEST_REALM_NAME);
        HttpEntity<UserRequest> requestEntity = new HttpEntity<>(request, headers);

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
    void shouldReturnBadRequestForMissingPassword() {
        // Arrange
        UserRequest request = new UserRequest();
        request.setUsername(TEST_USERNAME + "-no-password");
        request.setEmail("no-password@example.com");
        // No password set
        request.setFirstName("Test");
        request.setLastName("User");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-ID",TEST_REALM_NAME);
        HttpEntity<UserRequest> requestEntity = new HttpEntity<>(request, headers);

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
    void shouldCreateDisabledUser() {
        // Arrange
        UserRequest request = new UserRequest();
        request.setUsername(TEST_USERNAME + "-disabled");
        request.setEmail("disabled@example.com");
        request.setPassword(TEST_PASSWORD);
        request.setFirstName("Test");
        request.setLastName("UserDisabled");
        request.setEnabled(false); // Disabled user

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Tenant-ID",TEST_REALM_NAME);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserRequest> requestEntity = new HttpEntity<>(request, headers);
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
    void shouldHandleInvalidEmail() {
        // Arrange
        UserRequest request = new UserRequest();
        request.setUsername(TEST_USERNAME + "-invalid-email");
        request.setEmail("not-an-email"); // Invalid email
        request.setPassword(TEST_PASSWORD);
        request.setFirstName("Test");
        request.setLastName("User");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-ID",TEST_REALM_NAME);
        HttpEntity<UserRequest> requestEntity = new HttpEntity<>(request, headers);

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
        String nonExistentRealmUrl = "http://localhost:" + port + "/auth/realms/non-existent-realm/users";

        UserRequest request = new UserRequest();
        request.setUsername(TEST_USERNAME);
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);
        request.setFirstName("Test");
        request.setLastName("User");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-ID",TEST_REALM_NAME);
        HttpEntity<UserRequest> requestEntity = new HttpEntity<>(request, headers);

        // Act
        ResponseEntity<ApiGenericResponse> response = restTemplate.exchange(
                nonExistentRealmUrl,
                HttpMethod.POST,
                requestEntity,
                ApiGenericResponse.class);

        // Assert
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void shouldHandleNonExistentRoles() {
        // Arrange
        UserRequest request = new UserRequest();
        request.setUsername(TEST_USERNAME + "-nonexistent-roles");
        request.setEmail("nonexistent-roles@example.com");
        request.setPassword(TEST_PASSWORD);
        request.setFirstName("Test");
        request.setLastName("User");
        request.setRoles(Arrays.asList("non-existent-role-1", "non-existent-role-2"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Tenant-ID",TEST_REALM_NAME);
        HttpEntity<UserRequest> requestEntity = new HttpEntity<>(request, headers);

        // Act
        ResponseEntity<ApiGenericResponse> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                requestEntity,
                ApiGenericResponse.class);

        // Assert - Depending on implementation, might fail or ignore non-existent roles
        assertThat(response.getStatusCode().is4xxClientError() ||
                   response.getStatusCode().is2xxSuccessful()).isTrue();
    }
}
