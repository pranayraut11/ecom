package com.ecom.authprovider.manager.keycloak;

import com.ecom.authprovider.dto.request.UserRequest;
import com.ecom.authprovider.exception.KeycloakServiceException;
import com.ecom.authprovider.manager.api.UserManager;
import com.ecom.authprovider.util.KeycloakUtil;
import com.ecom.shared.common.config.common.TenantContext;
import jakarta.ws.rs.NotFoundException;
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
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of UserManager for handling Keycloak user operations.
 * This class manages user CRUD operations and role assignments in Keycloak.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakUserManager implements UserManager {

    private static final int CREATED_STATUS = 201;
    private static final String DEFAULT_ROLE = "user";
    private static final String REALM_MANAGEMENT_CLIENT = "realm-management";
    private static final String MANAGE_REALM_ROLE = "manage-realm";

    private final KeycloakUtil keycloakUtil;

    /**
     * Creates a regular user with the specified attributes and assigns roles.
     *
     * @param request User creation request containing username, password, first name, last name, email, and roles
     * @return the ID of the created user, or null if creation failed
     * @throws IllegalStateException if tenant context is not set
     * @throws IllegalArgumentException if required fields are missing
     */
    @Override
    public String createUser(UserRequest request) {
        validateUserRequest(request);

        // Apply default role if none provided
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            request.setRoles(List.of(DEFAULT_ROLE));
            log.info("No roles specified for user '{}', assigning default role '{}'",
                    request.getUsername(), DEFAULT_ROLE);
        }

        // Get and validate current tenant ID (realm name) from context
        request.setRealmName(getTenantRealm());

        // Use admin client for more reliable user creation
        return createUserInternal(request, false);
    }

    /**
     * Creates an admin user with the specified attributes and assigns admin roles.
     *
     * @param request User creation request containing username, password, first name, last name, email, and roles
     * @return the ID of the created admin user, or null if creation failed
     * @throws IllegalStateException if tenant context is not set
     * @throws IllegalArgumentException if required fields are missing
     */
    @Override
    public String createAdminUser(UserRequest request) {
        validateUserRequest(request);

        // Get and validate current tenant ID (realm name) from context
        if (!StringUtils.hasText(request.getRealmName())) {
            request.setRealmName(getTenantRealm());
        }

        return createUserInternal(request, true);
    }

    /**
     * Retrieves a user by their ID from Keycloak.
     *
     * @param userId ID of the user to retrieve
     * @return the UserRepresentation of the found user
     * @throws IllegalStateException if tenant context is not set
     * @throws NotFoundException if the user is not found
     * @throws KeycloakServiceException if there's an error retrieving the user
     */
    @Override
    public UserRepresentation getUserById(String userId) {
        if (!StringUtils.hasText(userId)) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        String realm = getTenantRealm();
        log.debug("Retrieving user with ID: {} from realm: {}", userId, realm);

        try (Keycloak keycloak = keycloakUtil.createAdminClient()) {
            RealmResource realmResource = keycloak.realm(realm);
            UserResource userResource = realmResource.users().get(userId);

            try {
                UserRepresentation user = userResource.toRepresentation();
                if (user == null) {
                    throw new NotFoundException("User not found with ID: " + userId);
                }
                return user;
            } catch (NotFoundException e) {
                throw new NotFoundException("User not found with ID: " + userId);
            }
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            String errorMsg = String.format("Error retrieving user with ID '%s': %s", userId, e.getMessage());
            log.error(errorMsg, e);
            throw new KeycloakServiceException(errorMsg, e);
        }
    }

    /**
     * Core method to create users in Keycloak with proper error handling.
     *
     * @param request the user request containing all user details
     * @param isAdmin whether to create an admin user with special privileges
     * @return the ID of the created user or null if creation failed
     */
    private String createUserInternal(UserRequest request, boolean isAdmin) {
        String username = request.getUsername();
        String realm = request.getRealmName();

        log.info("Creating {} user '{}' in realm '{}' with roles: {}",
                isAdmin ? "admin" : "regular", username, realm, request.getRoles());

        try (Keycloak keycloak = keycloakUtil.createAdminClient()) {
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();

            // Check if user already exists by username (more reliable than email)
            List<UserRepresentation> existingUsers = usersResource.search(username, true);
            if (!existingUsers.isEmpty()) {
                log.info("User '{}' already exists, updating roles", username);
                String userId = existingUsers.get(0).getId();
                boolean rolesAssigned = assignRolesToUser(userId, request.getRoles(), realmResource, isAdmin);
                return rolesAssigned ? userId : null;
            }

            // Create new user representation
            UserRepresentation user = createUserRepresentation(request);

            // Attempt to create the user
            try (Response response = usersResource.create(user)) {
                if (response.getStatus() != CREATED_STATUS) {
                    String errorMsg = String.format("Failed to create user '%s': Status %d",
                            username, response.getStatus());
                    log.error(errorMsg);

                    // Get error details from response if available
                    if (response.hasEntity()) {
                        try {
                            String errorBody = response.readEntity(String.class);
                            log.error("Error details: {}", errorBody);
                        } catch (Exception e) {
                            log.debug("Could not read error details", e);
                        }
                    }

                    return null;
                }

                // Extract user ID from response
                String userId = extractUserIdFromResponse(response);
                if (userId == null) {
                    return null;
                }

                // Set password and assign roles
                return completeUserCreation(usersResource, userId, request.getPassword(),
                        request.getRoles(), realmResource, isAdmin, username);
            }
        } catch (Exception e) {
            log.error("Unexpected error creating user '{}': {}", username, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Creates a UserRepresentation from the request data.
     */
    private UserRepresentation createUserRepresentation(UserRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUsername());
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        return user;
    }

    /**
     * Extracts the user ID from the Keycloak response.
     */
    private String extractUserIdFromResponse(Response response) {
        String location = response.getHeaderString("Location");
        String userId = location != null ? location.replaceAll(".*/([^/]+)$", "$1") : null;

        if (userId == null) {
            log.error("Failed to extract user ID from Location header");
            return null;
        }

        log.debug("User created with ID: {}", userId);
        return userId;
    }

    /**
     * Completes the user creation process by setting password and assigning roles.
     */
    private String completeUserCreation(UsersResource usersResource, String userId, String password,
                                        List<String> roles, RealmResource realmResource,
                                        boolean isAdmin, String username) {
        // Set password
        boolean passwordSet = setUserPassword(usersResource, userId, password);
        if (!passwordSet) {
            log.error("Failed to set password for user '{}'", username);
            return null;
        }

        // Assign roles
        boolean rolesAssigned = assignRolesToUser(userId, roles, realmResource, isAdmin);
        if (!rolesAssigned) {
            log.error("Failed to assign roles to user '{}'", username);
            return null;
        }

        log.info("Successfully created user '{}' with password and roles", username);
        return userId;
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
     * Assigns roles to a user, including special admin roles if requested.
     *
     * @param userId ID of the user
     * @param roleNames List of role names to assign
     * @param realmResource The realm resource
     * @param isAdmin Whether to assign admin privileges
     * @return true if roles were assigned successfully, false otherwise
     */
    private boolean assignRolesToUser(String userId, List<String> roleNames,
                                     RealmResource realmResource, boolean isAdmin) {
        log.debug("Assigning roles {} to user with ID '{}', isAdmin: {}",
                roleNames, userId, isAdmin);

        try {
            UserResource userResource = realmResource.users().get(userId);

            // Assign admin client roles if creating an admin user
            if (isAdmin) {
                assignAdminRoles(userId, realmResource);
            }

            // Assign regular realm roles
            if (roleNames != null && !roleNames.isEmpty()) {
                assignRealmRoles(roleNames, userResource, realmResource);
            }

            log.info("Successfully assigned roles to user");
            return true;
        } catch (Exception e) {
            log.error("Failed to assign roles to user: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Assigns admin roles to a user.
     */
    private void assignAdminRoles(String userId, RealmResource realmResource) {
        try {
            // Find the realm-management client
            String clientId = realmResource.clients().findByClientId(REALM_MANAGEMENT_CLIENT)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Realm management client not found"))
                    .getId();

            // Get the manage-realm role
            RoleRepresentation adminRole = realmResource.clients().get(clientId)
                    .roles().get(MANAGE_REALM_ROLE).toRepresentation();

            // Assign the role
            realmResource.users().get(userId)
                    .roles().clientLevel(clientId)
                    .add(Collections.singletonList(adminRole));

            log.debug("Assigned admin role '{}' to user", MANAGE_REALM_ROLE);
        } catch (Exception e) {
            log.error("Error assigning admin roles: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Assigns realm roles to a user.
     */
    private void assignRealmRoles(List<String> roleNames, UserResource userResource,
                                 RealmResource realmResource) {
        List<RoleRepresentation> rolesToAdd = roleNames.stream()
                .map(roleName -> {
                    try {
                        return realmResource.roles().get(roleName).toRepresentation();
                    } catch (Exception e) {
                        log.warn("Role '{}' not found, will be skipped", roleName);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        if (rolesToAdd.isEmpty()) {
            log.warn("No valid realm roles to assign to user");
            return;
        }

        userResource.roles().realmLevel().add(rolesToAdd);
        log.debug("Assigned realm roles: {}",
                rolesToAdd.stream().map(RoleRepresentation::getName).toList());
    }

    /**
     * Validates that the user request contains required fields.
     */
    private void validateUserRequest(UserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("User request cannot be null");
        }

        if (!StringUtils.hasText(request.getUsername())) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (!StringUtils.hasText(request.getPassword())) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (!StringUtils.hasText(request.getEmail())) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
    }

    /**
     * Gets the tenant realm from context and validates it.
     */
    private String getTenantRealm() {
        String realm = TenantContext.getTenantId();
        if (!StringUtils.hasText(realm)) {
            log.error("Cannot create user: Tenant ID is not set in TenantContext");
            throw new IllegalStateException("Tenant ID is not set in TenantContext");
        }
        return realm;
    }
}
