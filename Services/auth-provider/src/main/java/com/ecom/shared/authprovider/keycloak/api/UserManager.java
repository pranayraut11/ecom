package com.ecom.shared.authprovider.keycloak.api;

import java.util.List;

/**
 * Interface for user management operations.
 */
public interface UserManager {

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
    String createUser(String username, String password, String firstName,
                     String lastName, String email, List<String> roles);
}
