package com.ecom.authprovider.manager.api;

import org.keycloak.representations.idm.RealmRepresentation;

import java.util.List;
/**
 * Interface for realm management operations.
 */
public interface RealmManager {

    /**
     * Creates a new realm if it doesn't already exist.
     *
     * @param realmName Name of the realm to create
     * @return true if realm was created or already exists, false otherwise
     */
    boolean createRealm(String realmName);

    /**
     * Deletes a realm if it exists.
     *
     * @param realmName Name of the realm to delete
     * @return true if realm was deleted or didn't exist, false if deletion failed
     */
    boolean deleteRealm(String realmName);


    /**
     * Checks if a realm exists.
     *
     * @param realmName Name of the realm to check
     * @return true if the realm exists, false otherwise
     */
    boolean realmExists(String realmName);

    /**
     * Gets a realm representation by name.
     *
     * @param realmName Name of the realm to retrieve
     * @return RealmRepresentation of the specified realm
     * @throws jakarta.ws.rs.NotFoundException if realm doesn't exist
     */
    RealmRepresentation getRealmByName(String realmName);

    /**
     * Gets all realms.
     *
     * @return List of all realm representations
     */
    List<RealmRepresentation> getAllRealms();

    /**
     * Updates a realm.
     *
     * @param realmName Name of the realm to update
     * @param representation RealmRepresentation with updated values
     * @return true if realm was updated successfully, false otherwise
     */
    boolean updateRealm(String realmName, RealmRepresentation representation);

    /**
     * Enables or disables a realm.
     *
     * @param realmName Name of the realm to update
     * @param enabled Whether the realm should be enabled or disabled
     * @return true if the operation was successful, false otherwise
     */
    boolean setRealmEnabled(String realmName, boolean enabled);
}
