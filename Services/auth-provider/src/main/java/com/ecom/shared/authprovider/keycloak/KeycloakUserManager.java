package com.ecom.shared.authprovider.keycloak;

import com.ecom.shared.authprovider.keycloak.api.RoleManager;
import com.ecom.shared.authprovider.keycloak.api.UserManager;
import jakarta.annotation.PreDestroy;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of UserManager for handling Keycloak user operations.
 */
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserManager implements UserManager {

    private static final int CREATED_STATUS = 201;

    private final RealmResource realmResource;
    private final RoleManager roleManager;
    private final Keycloak keycloak;  // Store the Keycloak client

    @PreDestroy
    public void cleanup() {
        // Close the Keycloak client when this bean is destroyed
        KeycloakAdminClientFactory.closeKeycloakClient(keycloak);
        log.debug("Closed Keycloak client in UserManager");
    }

    /**
     * Creates a user with the specified attributes and assigns roles.
     *
     * @param username Username for the new user
     * @param password Password for the new user
     * @param firstName First name of the user
     * @param lastName Last name of the user
     * @param email Email address of the user
     * @param roles List of role names to assign
     * @return the ID of the created user, or null if creation failed
     */
    public String createUser(String username, String password, String firstName,
                            String lastName, String email, List<String> roles) {
        log.info("Creating user '{}' with roles: {}", username, roles);

        UsersResource usersResource = realmResource.users();

        // Check if user already exists
        List<UserRepresentation> existingUsers = usersResource.searchByEmail(email, true);
        if (!existingUsers.isEmpty()) {
            log.info("User '{}' already exists, updating roles", username);
            String userId = existingUsers.get(0).getId();
            boolean rolesAssigned = assignRolesToUser(userId, roles);
            return rolesAssigned ? userId : null;
        }

        // Create new user
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        try (Response response = usersResource.create(user)) {
            if (response.getStatus() != CREATED_STATUS) {
                log.error("Failed to create user: Status {}", response.getStatus());
                return null;
            }

            // Get user ID from response
            String userId = KeycloakAdminClientFactory.getCreatedId(response);
            if (userId == null) {
                log.error("Failed to get created user ID");
                return null;
            }

            // Set password
            boolean passwordSet = setUserPassword(usersResource, userId, password);
            if (!passwordSet) {
                log.error("Failed to set password for user '{}'", username);
                return null;
            }

            // Assign roles
            boolean rolesAssigned = assignRolesToUser(userId, roles);
            if (!rolesAssigned) {
                log.error("Failed to assign roles to user '{}'", username);
                return null;
            }

            log.info("Successfully created user '{}' with password and roles", username);
            return userId;
        } catch (Exception e) {
            log.error("Unexpected error creating user '{}'", username, e);
            return null;
        }
    }

    /**
     * Sets a password for the specified user.
     *
     * @param usersResource Users resource
     * @param userId ID of the user
     * @param password Password to set
     * @return true if password was set successfully, false otherwise
     */
    private boolean setUserPassword(UsersResource usersResource, String userId, String password) {
        log.debug("Setting password for user with ID '{}'", userId);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        try {
            UserResource userResource = usersResource.get(userId);
            userResource.resetPassword(credential);
            return true;
        } catch (Exception e) {
            log.error("Failed to set password for user with ID '{}'", userId, e);
            return false;
        }
    }

    /**
     * Assigns roles to a user.
     *
     * @param userId ID of the user
     * @param roleNames List of role names to assign
     * @return true if roles were assigned successfully, false otherwise
     */
    private boolean assignRolesToUser(String userId, List<String> roleNames) {
        log.debug("Assigning roles {} to user with ID '{}'", roleNames, userId);

        try {
            UserResource userResource = realmResource.users().get(userId);
            List<RoleRepresentation> rolesToAdd = roleNames.stream()
                    .map(roleName -> {
                        RoleRepresentation role = roleManager.getRealmRole(roleName);
                        if (role == null) {
                            log.warn("Role '{}' not found, will be skipped", roleName);
                        }
                        return role;
                    })
                    .filter(role -> role != null)
                    .toList();

            if (rolesToAdd.isEmpty()) {
                log.warn("No valid roles to assign to user");
                return false;
            }

            userResource.roles().realmLevel().add(rolesToAdd);
            log.info("Successfully assigned roles to user");
            return true;
        } catch (Exception e) {
            log.error("Failed to assign roles to user", e);
            return false;
        }
    }
}
