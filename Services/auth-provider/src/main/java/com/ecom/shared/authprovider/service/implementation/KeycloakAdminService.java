package com.ecom.shared.authprovider.service.implementation;

import com.ecom.shared.authprovider.service.specification.AdminService;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Implementation of AdminService using Keycloak Admin API
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakAdminService implements AdminService {

    private static final String ALL_URLS = "*";
    private static final String CLIENT_SECRET_AUTHENTICATOR = "client-secret";
    private static final String ALREADY_EXISTS_ERROR = "already exists";
    private static final int CREATED_STATUS = 201;
    private static final int BAD_REQUEST_STATUS = 400;

    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.master-realm}")
    private String keycloakMasterRealm;

    @Value("${keycloak.admin.username}")
    private String keycloakAdminUsername;

    @Value("${keycloak.admin.password}")
    private String keycloakAdminPassword;

    @Value("${keycloak.admin.client-id}")
    private String keycloakAdminClientId;

    @Override
    public void createRealm(String realmName) {
        // Initialize Keycloak admin client
        Keycloak keycloak = getKeycloakInstance();
        try {
            // === 1. Create Realm if not exists ===
            boolean realmExists = keycloak.realms().findAll().stream()
                    .anyMatch(r -> r.getRealm().equals(realmName));

            if (!realmExists) {
                RealmRepresentation realm = new RealmRepresentation();
                realm.setRealm(realmName);
                realm.setEnabled(true);
                realm.setDisplayName(realmName);

                try {
                    keycloak.realms().create(realm);
                    log.info("Realm created: {}", realmName);
                } catch (WebApplicationException e) {
                    try (Response response = e.getResponse()) {
                        String errorBody = response.readEntity(String.class);
                        log.error("Failed to create realm: Status {}, Details: {}", response.getStatus(), errorBody);
                        if (response.getStatus() == BAD_REQUEST_STATUS && errorBody.contains(ALREADY_EXISTS_ERROR)) {
                            log.info("Realm '{}' already exists, continuing with configuration", realmName);
                        } else {
                            throw e;
                        }
                    }
                }
            } else {
                log.info("Realm '{}' already exists, continuing with configuration", realmName);
            }

            RealmResource realmResource = keycloak.realm(realmName);

            // === 2. Create Clients ===
            createClient(realmResource, "frontend-app", true, null);
            createClient(realmResource, "backend-service", false, "backend-secret");

            // === 3. Create Roles ===
            createRole(realmResource, "user");
            createRole(realmResource, "admin");

            // === 4. Create Admin User with roles ===
            createAdminUser(realmResource);
        } finally {
            // Close Keycloak instance if it's closeable
            if (keycloak instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) keycloak).close();
                } catch (Exception e) {
                    log.warn("Error closing Keycloak client", e);
                }
            }
        }
    }

    /**
     * Creates and returns a Keycloak admin client instance
     */
    private Keycloak getKeycloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakServerUrl)
                .realm(keycloakMasterRealm)
                .username(keycloakAdminUsername)
                .password(keycloakAdminPassword)
                .clientId(keycloakAdminClientId)
                .grantType(OAuth2Constants.PASSWORD)
                .build();
    }

    /**
     * Creates a client in the specified realm if it doesn't already exist
     *
     * @param realmResource the realm where to create the client
     * @param clientId the client ID
     * @param isPublic whether the client is public or confidential
     * @param clientSecret secret for confidential clients
     */
    private void createClient(RealmResource realmResource, String clientId, boolean isPublic, String clientSecret) {
        try {
            // Check if client already exists
            boolean clientExists = realmResource.clients().findAll().stream()
                    .anyMatch(client -> client.getClientId().equals(clientId));

            if (!clientExists) {
                ClientRepresentation client = new ClientRepresentation();
                client.setClientId(clientId);
                client.setEnabled(true);
                client.setDirectAccessGrantsEnabled(true);
                client.setRedirectUris(Collections.singletonList(ALL_URLS));
                client.setWebOrigins(Collections.singletonList(ALL_URLS));

                if (isPublic) {
                    client.setPublicClient(true);
                    client.setBearerOnly(false);
                } else {
                    client.setPublicClient(false);
                    client.setBearerOnly(false);
                    client.setClientAuthenticatorType(CLIENT_SECRET_AUTHENTICATOR);
                    client.setSecret(clientSecret);
                }

                try (Response response = realmResource.clients().create(client)) {
                    if (response.getStatus() == CREATED_STATUS) {
                        log.info("Client '{}' created successfully", clientId);
                    } else {
                        String errorBody = response.readEntity(String.class);
                        log.error("Failed to create client: Status {}, Details: {}", response.getStatus(), errorBody);
                    }
                }
            } else {
                log.info("Client '{}' already exists", clientId);
            }
        } catch (WebApplicationException e) {
            try (Response response = e.getResponse()) {
                String errorBody = response.readEntity(String.class);
                log.error("Failed to create client: Status {}, Details: {}", response.getStatus(), errorBody);
                if (response.getStatus() != BAD_REQUEST_STATUS || !errorBody.contains(ALREADY_EXISTS_ERROR)) {
                    throw e;
                }
            }
        }
    }

    /**
     * Creates a role in the specified realm if it doesn't already exist
     *
     * @param realmResource the realm where to create the role
     * @param roleName the name of the role
     */
    private void createRole(RealmResource realmResource, String roleName) {
        RolesResource rolesResource = realmResource.roles();

        try {
            // Check if role already exists
            if (rolesResource.list().stream().noneMatch(role -> role.getName().equals(roleName))) {
                RoleRepresentation role = new RoleRepresentation();
                role.setName(roleName);
                role.setDescription("Role for " + roleName);

                rolesResource.create(role);
                log.info("Role '{}' created successfully", roleName);
            } else {
                log.info("Role '{}' already exists", roleName);
            }
        } catch (WebApplicationException e) {
            try (Response response = e.getResponse()) {
                String errorBody = response.readEntity(String.class);
                log.error("Failed to create role: Status {}, Details: {}", response.getStatus(), errorBody);
                if (response.getStatus() != BAD_REQUEST_STATUS || !errorBody.contains(ALREADY_EXISTS_ERROR)) {
                    throw e;
                }
            }
        }
    }

    /**
     * Creates an admin user in the specified realm if it doesn't already exist
     * and assigns appropriate roles
     *
     * @param realmResource the realm where to create the user
     */
    private void createAdminUser(RealmResource realmResource) {
        UsersResource usersResource = realmResource.users();

        // Check if user already exists
        List<UserRepresentation> existingUsers = usersResource.search("adminuser", 0, 1);
        if (!existingUsers.isEmpty()) {
            log.info("User 'adminuser' already exists");
            // Update roles for existing user
            assignRolesToUser(realmResource, existingUsers.get(0).getId());
            return;
        }

        // Create new user
        UserRepresentation user = new UserRepresentation();
        user.setUsername("adminuser");
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setFirstName("Admin");
        user.setLastName("User");
        user.setEmail("admin@example.com");

        try (Response response = usersResource.create(user)) {
            if (response.getStatus() != CREATED_STATUS) {
                log.error("Failed to create user: Status {}", response.getStatus());
                return;
            }

            // Get user ID from response
            String userId = getCreatedId(response);

            // Set password for new user
            setUserPassword(usersResource, userId, "Admin@123");

            // Assign roles
            assignRolesToUser(realmResource, userId);

            log.info("User 'adminuser' created successfully with password and roles");
        }
    }

    /**
     * Extracts the created entity ID from a Response object
     *
     * @param response the Response object containing the Location header
     * @return the ID of the created entity
     */
    private String getCreatedId(Response response) {
        String locationHeader = response.getHeaderString("Location");
        if (locationHeader != null) {
            return locationHeader.replaceAll(".*/([^/]+)$", "$1");
        }
        return null;
    }

    /**
     * Sets a password for the specified user
     *
     * @param usersResource the users resource
     * @param userId the ID of the user
     * @param password the password to set
     */
    private void setUserPassword(UsersResource usersResource, String userId, String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        UserResource userResource = usersResource.get(userId);
        userResource.resetPassword(credential);
    }

    /**
     * Assigns roles to a user
     *
     * @param realmResource the realm resource
     * @param userId the ID of the user
     */
    private void assignRolesToUser(RealmResource realmResource, String userId) {
        UserResource userResource = realmResource.users().get(userId);

        // Get realm roles
        RoleRepresentation userRole = realmResource.roles().get("user").toRepresentation();
        RoleRepresentation adminRole = realmResource.roles().get("admin").toRepresentation();

        // Assign roles to user
        userResource.roles().realmLevel().add(Arrays.asList(userRole, adminRole));
    }

    @Override
    public void deleteRealm(String realmName) {
        // Implementation for deleteRealm
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void updateRealm(String realmName, Object realmConfig) {
        // Implementation for updateRealm
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Object getRealm(String realmName) {
        // Implementation for getRealm
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
