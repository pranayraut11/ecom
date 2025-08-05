package com.ecom.shared.authprovider.keycloak;

import com.ecom.shared.authprovider.keycloak.api.RealmManager;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RealmRepresentation;

/**
 * Implementation of RealmManager for handling Keycloak realm operations.
 */
@Slf4j
public class KeycloakRealmManager implements RealmManager {

    private static final int BAD_REQUEST_STATUS = 400;
    private static final String ALREADY_EXISTS_ERROR = "already exists";

    private final Keycloak keycloak;

    /**
     * Constructs a RealmManager with the provided Keycloak client.
     *
     * @param keycloak Keycloak admin client
     */
    public KeycloakRealmManager(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    /**
     * Creates a new realm if it doesn't already exist.
     *
     * @param realmName Name of the realm to create
     * @return true if realm was created or already exists, false otherwise
     */
    public boolean createRealm(String realmName) {
        log.info("Checking if realm '{}' exists", realmName);

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

    /**
     * Deletes a realm if it exists.
     *
     * @param realmName Name of the realm to delete
     * @return true if realm was deleted or didn't exist, false if deletion failed
     */
    public boolean deleteRealm(String realmName) {
        log.info("Attempting to delete realm '{}'", realmName);

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
