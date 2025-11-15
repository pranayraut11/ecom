package com.ecom.authprovider.manager.keycloak;

import com.ecom.authprovider.manager.api.RoleManager;
import com.ecom.authprovider.util.KeycloakUtil;
import com.ecom.shared.common.config.common.TenantContext;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Implementation of RoleManager for handling Keycloak role operations.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class KeycloakRoleManager implements RoleManager {

    private static final int BAD_REQUEST_STATUS = 400;
    private static final String ALREADY_EXISTS_ERROR = "already exists";
    private final KeycloakUtil config;

//    @PreDestroy
//    public void cleanup() {
//        // Close the Keycloak client when this bean is destroyed
//        KeycloakAdminConfig.closeKeycloakClient(keycloak);
//        log.debug("Closed Keycloak client in RoleManager");
//    }

    /**
     * Creates a realm role if it doesn't already exist.
     *
     * @param roleName Name of the role to create
     * @return true if role was created or already exists, false otherwise
     */
    public boolean createRealmRole(String roleName,String realmName) {
        log.info("Creating realm role '{}'", roleName);

        try (Keycloak keycloak = config.createAdminClient()) {

            RolesResource rolesResource = keycloak.realm(realmName).roles();

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

    }

    @Override
    public RoleRepresentation getRealmRole(String roleName) {
        try (Keycloak keycloak = config.createAdminClient()) {
            RolesResource rolesResource = keycloak.realm(TenantContext.getTenantId()).roles();

            // Check if role already exists
            try {
                return rolesResource.get(roleName).toRepresentation();
            } catch (WebApplicationException e) {

            }
        }
        return null;
    }

    @Override
    public boolean deleteRealmRole(String roleName, String realmName) {
        log.info("Deleting realm role '{}'", roleName);

        try (Keycloak keycloak = config.createAdminClient()) {

            RolesResource rolesResource = keycloak.realm(realmName).roles();

            // Check if role exists
            try {
                RoleRepresentation existingRole = rolesResource.get(roleName).toRepresentation();
                if (existingRole == null) {
                    log.info("Role '{}' does not exist, nothing to delete", roleName);
                    return true;
                }
            } catch (Exception e) {
                // Role doesn't exist
                log.info("Role '{}' does not exist, nothing to delete", roleName);
                return true;
            }

            // Delete role
            try {
                rolesResource.deleteRole(roleName);
                log.info("Successfully deleted role '{}'", roleName);
                return true;
            } catch (WebApplicationException e) {
                try (Response response = e.getResponse()) {
                    String errorBody = response.readEntity(String.class);
                    log.error("Failed to delete role: Status {}, Details: {}",
                            response.getStatus(), errorBody);
                }
                return false;
            } catch (Exception e) {
                log.error("Unexpected error deleting role '{}'", roleName, e);
                return false;
            }
        }
    }
}
