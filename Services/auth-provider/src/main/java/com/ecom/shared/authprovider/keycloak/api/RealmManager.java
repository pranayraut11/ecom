package com.ecom.shared.authprovider.keycloak.api;

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
}
