package com.ecom.shared.authprovider.keycloak;

import com.ecom.shared.authprovider.keycloak.api.RoleManager;
import jakarta.annotation.PreDestroy;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;

/**
 * Implementation of RoleManager for handling Keycloak role operations.
 */
@Slf4j
@RequiredArgsConstructor
public class KeycloakRoleManager implements RoleManager {

    private static final int BAD_REQUEST_STATUS = 400;
    private static final String ALREADY_EXISTS_ERROR = "already exists";

    private final RealmResource realmResource;
    private final Keycloak keycloak;  // Store the Keycloak client

    @PreDestroy
    public void cleanup() {
        // Close the Keycloak client when this bean is destroyed
        KeycloakAdminClientFactory.closeKeycloakClient(keycloak);
        log.debug("Closed Keycloak client in RoleManager");
    }

    /**
     * Creates a realm role if it doesn't already exist.
     *
     * @param roleName Name of the role to create
     * @return true if role was created or already exists, false otherwise
     */
    public boolean createRealmRole(String roleName) {
        log.info("Creating realm role '{}'", roleName);

        RolesResource rolesResource = realmResource.roles();

        // Check if role already exists
        try {
            RoleRepresentation existingRole = rolesResource.get(roleName).toRepresentation();
            if (existingRole != null) {
                log.info("Role '{}' already exists", roleName);
                return true;
            }
        } catch (Exception e) {
            // Role doesn't exist, continue with creation
            log.debug("Role '{}' doesn't exist, creating new role", roleName);
        }

        // Create role
        RoleRepresentation role = new RoleRepresentation();
        role.setName(roleName);
        role.setDescription("Role for " + roleName);

        try {
            rolesResource.create(role);
            log.info("Successfully created role '{}'", roleName);
            return true;
        } catch (WebApplicationException e) {
            try (Response response = e.getResponse()) {
                String errorBody = response.readEntity(String.class);
                log.error("Failed to create role: Status {}, Details: {}",
                        response.getStatus(), errorBody);

                // If role already exists (race condition), consider it a success
                if (response.getStatus() == BAD_REQUEST_STATUS &&
                        errorBody.contains(ALREADY_EXISTS_ERROR)) {
                    log.info("Role '{}' already exists (concurrent creation)", roleName);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("Unexpected error creating role '{}'", roleName, e);
            return false;
        }
    }

    /**
     * Gets a role representation by name.
     *
     * @param roleName Name of the role to retrieve
     * @return RoleRepresentation if found, null otherwise
     */
    public RoleRepresentation getRealmRole(String roleName) {
        try {
            return realmResource.roles().get(roleName).toRepresentation();
        } catch (Exception e) {
            log.error("Error retrieving role '{}'", roleName, e);
            return null;
        }
    }
}
