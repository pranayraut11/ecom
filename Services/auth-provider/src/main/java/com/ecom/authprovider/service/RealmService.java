package com.ecom.authprovider.service;

import com.ecom.authprovider.dto.request.RealmRequest;
import com.ecom.authprovider.dto.request.UserRequest;
import com.ecom.authprovider.dto.response.RealmResponse;
import com.ecom.authprovider.exception.KeycloakServiceException;
import com.ecom.authprovider.manager.api.RealmManager;
import com.ecom.authprovider.manager.api.RoleManager;
import com.ecom.authprovider.manager.api.UserManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service for managing Keycloak realms.
 * Handles realm creation and setup operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RealmService {

    private final RealmManager realmManager;
    private final RoleManager roleManager;
    private final UserManager userManager;

    @Value("${keycloak.default-admin-username:admin}")
    private String defaultAdminUsername;

    @Value("${keycloak.default-admin-password:admin}")
    private String defaultAdminPassword;

    @Value("${keycloak.default-admin-firstname:Admin}")
    private String defaultAdminFirstname;

    @Value("${keycloak.default-admin-lastname:User}")
    private String defaultAdminLastname;

    @Value("${keycloak.default-admin-email:admin@example.com}")
    private String defaultAdminEmail;

    /**
     * Creates a new realm in Keycloak based on the provided request.
     *
     * @param request the realm creation request
     * @return response containing creation status and realm details
     * @throws KeycloakServiceException if realm creation fails
     * @throws IllegalArgumentException if the request parameters are invalid
     */
    public boolean createRealm(RealmRequest request) {
        validateRealmRequest(request);
        String realmName = request.getName();

        log.info("Creating realm with name: {}", realmName);

        try {
            boolean created = realmManager.createRealm(realmName);

            if (!created) {
                String errorMessage = String.format("Failed to create realm '%s'", realmName);
                log.error(errorMessage);
                throw new KeycloakServiceException(errorMessage);
            }

            log.info("Realm '{}' created successfully", realmName);

            // Setup realm asynchronously to improve response time
            setupRealmAsync(realmName);

            return true;
        } catch (KeycloakServiceException e) {
            // Already formatted exception, just rethrow
            throw e;
        } catch (Exception e) {
            String errorMessage = String.format("Error creating realm '%s': %s", realmName, e.getMessage());
            log.error(errorMessage, e);
            throw new KeycloakServiceException(errorMessage, e);
        }
    }

    /**
     * Checks if a realm exists.
     *
     * @param realmName the name of the realm to check
     * @return true if the realm exists, false otherwise
     */
    public boolean realmExists(String realmName) {
        if (!StringUtils.hasText(realmName)) {
            throw new IllegalArgumentException("Realm name cannot be empty");
        }

        try {
            return realmManager.realmExists(realmName);
        } catch (Exception e) {
            String errorMessage = String.format("Error checking if realm '%s' exists: %s", realmName, e.getMessage());
            log.error(errorMessage, e);
            throw new KeycloakServiceException(errorMessage, e);
        }
    }

    /**
     * Gets realm details by name.
     *
     * @param realmName the name of the realm
     * @return response containing realm details
     */
    public RealmResponse getRealmByName(String realmName) {
        if (!StringUtils.hasText(realmName)) {
            throw new IllegalArgumentException("Realm name cannot be empty");
        }

        try {
            if (!realmManager.realmExists(realmName)) {
                throw new KeycloakServiceException(String.format("Realm '%s' not found", realmName));
            }

            // In a real implementation, you would get more realm details here
            return RealmResponse.builder()
                    .name(realmName)
                    .enabled(true)
                    .build();
        } catch (KeycloakServiceException e) {
            throw e;
        } catch (Exception e) {
            String errorMessage = String.format("Error getting realm '%s': %s", realmName, e.getMessage());
            log.error(errorMessage, e);
            throw new KeycloakServiceException(errorMessage, e);
        }
    }

    /**
     * Validates the realm request parameters.
     *
     * @param request the realm creation request to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateRealmRequest(RealmRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Realm request cannot be null");
        }

        String realmName = request.getName();
        if (!StringUtils.hasText(realmName)) {
            throw new IllegalArgumentException("Realm name cannot be empty");
        }

        // Check if realm already exists
        if (realmExists(realmName)) {
            throw new KeycloakServiceException(String.format("Realm '%s' already exists", realmName));
        }
    }

    /**
     * Sets up a newly created realm asynchronously.
     * Creates default roles and admin user.
     *
     * @param realmName the name of the realm to set up
     */
    private void setupRealmAsync(String realmName) {
        CompletableFuture.runAsync(() -> {
            try {
                // Create default admin role
                createDefaultAdminRole(realmName);

                // Create admin user
                createDefaultAdminUser(realmName);

                log.info("Completed realm '{}' setup successfully", realmName);
            } catch (Exception e) {
                log.error("Error during realm '{}' setup: {}", realmName, e.getMessage(), e);
                // We don't throw the exception since this is an async operation
                // and we don't want to block the main thread
            }
        });
    }

    /**
     * Creates the default admin role in the realm.
     *
     * @param realmName the name of the realm
     */
    private void createDefaultAdminRole(String realmName) {
        try {
            boolean created = roleManager.createRealmRole("Admin", realmName);
            if (created) {
                log.info("Created 'Admin' role in realm '{}'", realmName);
            } else {
                log.warn("'Admin' role already exists in realm '{}'", realmName);
            }
        } catch (Exception e) {
            log.error("Failed to create 'Admin' role in realm '{}': {}", realmName, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Creates the default admin user in the realm.
     *
     * @param realmName the name of the realm
     */
    private void createDefaultAdminUser(String realmName) {
        try {
            UserRequest adminUserRequest = buildAdminUserRequest(realmName);

            String userId = userManager.createAdminUser(adminUserRequest);
            if (StringUtils.hasText(userId)) {
                log.info("Created admin user '{}' in realm '{}'", defaultAdminUsername, realmName);
            } else {
                log.warn("Failed to create admin user in realm '{}'", realmName);
            }
        } catch (Exception e) {
            log.error("Failed to create admin user in realm '{}': {}", realmName, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Builds the admin user request with configured or default values.
     *
     * @param realmName the name of the realm
     * @return the admin user request
     */
    private UserRequest buildAdminUserRequest(String realmName) {
        UserRequest adminUserRequest = new UserRequest();
        adminUserRequest.setUsername(defaultAdminUsername);
        adminUserRequest.setPassword(defaultAdminPassword);
        adminUserRequest.setFirstName(defaultAdminFirstname);
        adminUserRequest.setLastName(defaultAdminLastname);
        adminUserRequest.setEmail(defaultAdminEmail);
        adminUserRequest.setRoles(List.of("Admin"));
        adminUserRequest.setRealmName(realmName);
        return adminUserRequest;
    }
}
