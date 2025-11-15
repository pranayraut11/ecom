package com.ecom.authprovider.manager.api;

import org.keycloak.representations.idm.RoleRepresentation;

/**
 * Interface for role management operations.
 */
public interface RoleManager {

    /**
     * Creates a realm role if it doesn't already exist.
     *
     * @param roleName Name of the role to create
     * @return true if role was created or already exists, false otherwise
     */
    boolean createRealmRole(String roleName,String realmName);

    /**
     * Gets a role representation by name.
     *
     * @param roleName Name of the role to retrieve
     * @return RoleRepresentation if found, null otherwise
     */
    RoleRepresentation getRealmRole(String roleName);

    /**
     * Deletes a realm role by name.
     *
     * @param roleName Name of the role to delete
     * @return true if role was deleted, false otherwise
     */
    boolean deleteRealmRole(String roleName,String realmName);
}
