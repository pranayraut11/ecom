package com.ecom.authprovider.manager.keycloak;

import com.ecom.authprovider.manager.api.RealmManager;
import com.ecom.authprovider.util.KeycloakUtil;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of RealmManager for handling Keycloak realm operations.
 */
@Slf4j
@Component
public class KeycloakRealmManager implements RealmManager {

    private static final int BAD_REQUEST_STATUS = 400;
    private static final String ALREADY_EXISTS_ERROR = "already exists";

    private final KeycloakUtil keycloakUtil;

    /**
     * Constructs a RealmManager with the provided Keycloak client.
     *
     * @param keycloak Keycloak admin client
     */
    public KeycloakRealmManager(KeycloakUtil keycloak) {
        this.keycloakUtil = keycloak;
    }

    /**
     * Creates a new realm if it doesn't already exist.
     *
     * @param realmName Name of the realm to create
     * @return true if realm was created or already exists, false otherwise
     */
    public boolean createRealm(String realmName) {
        log.info("Checking if realm '{}' exists", realmName);
        try(Keycloak keycloak =  keycloakUtil.createAdminClient()) {
            // Check if realm already exists
            boolean realmExists = keycloak.realms().findAll().stream()
                    .anyMatch(r -> r.getRealm().equals(realmName));

            if (realmExists) {
                log.info("Realm '{}' already exists", realmName);
                return true;
            }

            // Create new realm
            log.info("Creating new realm '{}'", realmName);
            RealmRepresentation realm = new RealmRepresentation();
            realm.setRealm(realmName);
            realm.setEnabled(true);
            realm.setDisplayName(realmName);

            try {
                keycloak.realms().create(realm);
                log.info("Successfully created realm '{}'", realmName);
                return true;
            } catch (WebApplicationException e) {
                try (Response response = e.getResponse()) {
                    String errorBody = response.readEntity(String.class);
                    log.error("Failed to create realm: Status {}, Details: {}",
                            response.getStatus(), errorBody);

                    // If realm already exists (race condition), consider it a success
                    if (response.getStatus() == BAD_REQUEST_STATUS &&
                            errorBody.contains(ALREADY_EXISTS_ERROR)) {
                        log.info("Realm '{}' already exists (concurrent creation)", realmName);
                        return true;
                    } else {
                        log.error("Failed to create realm '{}'", realmName, e);
                        return false;
                    }
                }
            } catch (Exception e) {
                log.error("Unexpected error creating realm '{}'", realmName, e);
                return false;
            }
        }
    }

    /**
     * Deletes a realm if it exists.
     *
     * @param realmName Name of the realm to delete
     * @return true if realm was deleted or didn't exist, false if deletion failed
     */
    public boolean deleteRealm(String realmName) {
        log.info("Attempting to delete realm '{}'", realmName);
        try(Keycloak keycloak =  keycloakUtil.createAdminClient()) {
            try {
                keycloak.realm(realmName).remove();
                log.info("Successfully deleted realm '{}'", realmName);
                return true;
            } catch (Exception e) {
                log.error("Failed to delete realm '{}'", realmName, e);
                return false;
            }
        }
    }

    /**
     * Checks if a realm exists.
     *
     * @param realmName Name of the realm to check
     * @return true if the realm exists, false otherwise
     */
    @Override
    public boolean realmExists(String realmName) {
        log.debug("Checking if realm '{}' exists", realmName);
        try (Keycloak keycloak = keycloakUtil.createAdminClient()) {
            return keycloak.realms().findAll().stream()
                    .anyMatch(r -> r.getRealm().equals(realmName));
        } catch (Exception e) {
            log.error("Error checking if realm '{}' exists", realmName, e);
            return false;
        }
    }

    /**
     * Gets a realm representation by name.
     *
     * @param realmName Name of the realm to retrieve
     * @return RealmRepresentation of the specified realm
     * @throws NotFoundException if realm doesn't exist
     */
    @Override
    public RealmRepresentation getRealmByName(String realmName) {
        log.debug("Getting realm representation for '{}'", realmName);
        try (Keycloak keycloak = keycloakUtil.createAdminClient()) {
            try {
                return keycloak.realm(realmName).toRepresentation();
            } catch (NotFoundException e) {
                log.error("Realm '{}' not found", realmName);
                throw e;
            } catch (Exception e) {
                log.error("Error getting realm '{}' representation", realmName, e);
                throw new RuntimeException("Failed to get realm: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Gets all realms.
     *
     * @return List of all realm representations
     */
    @Override
    public List<RealmRepresentation> getAllRealms() {
        log.debug("Getting all realms");
        try (Keycloak keycloak = keycloakUtil.createAdminClient()) {
            return keycloak.realms().findAll();
        } catch (Exception e) {
            log.error("Error getting all realms", e);
            return Collections.emptyList();
        }
    }

    /**
     * Updates a realm.
     *
     * @param realmName Name of the realm to update
     * @param representation RealmRepresentation with updated values
     * @return true if realm was updated successfully, false otherwise
     */
    @Override
    public boolean updateRealm(String realmName, RealmRepresentation representation) {
        log.info("Updating realm '{}'", realmName);
        try (Keycloak keycloak = keycloakUtil.createAdminClient()) {
            try {
                // Ensure the realm name in the representation matches the specified realm name
                representation.setRealm(realmName);

                // Update the realm
                keycloak.realm(realmName).update(representation);
                log.info("Successfully updated realm '{}'", realmName);
                return true;
            } catch (NotFoundException e) {
                log.error("Realm '{}' not found for update", realmName);
                return false;
            } catch (Exception e) {
                log.error("Failed to update realm '{}'", realmName, e);
                return false;
            }
        }
    }

    /**
     * Enables or disables a realm.
     *
     * @param realmName Name of the realm to update
     * @param enabled Whether the realm should be enabled or disabled
     * @return true if the operation was successful, false otherwise
     */
    @Override
    public boolean setRealmEnabled(String realmName, boolean enabled) {
        log.info("{} realm '{}'", enabled ? "Enabling" : "Disabling", realmName);
        try (Keycloak keycloak = keycloakUtil.createAdminClient()) {
            try {
                RealmRepresentation realm = keycloak.realm(realmName).toRepresentation();
                realm.setEnabled(enabled);
                keycloak.realm(realmName).update(realm);
                log.info("Successfully {} realm '{}'", enabled ? "enabled" : "disabled", realmName);
                return true;
            } catch (NotFoundException e) {
                log.error("Realm '{}' not found for {} operation", realmName, enabled ? "enable" : "disable");
                return false;
            } catch (Exception e) {
                log.error("Failed to {} realm '{}'", enabled ? "enable" : "disable", realmName, e);
                return false;
            }
        }
    }
}
