package com.ecom.authprovider.manager.api;

import com.ecom.authprovider.dto.request.UserRequest;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

/**
 * Interface for user management operations.
 */
public interface UserManager {

    /**
     * Creates a user with the specified attributes and assigns roles.
     * @param request User creation request containing username, password, first name, last name, email, and roles
     * @return the ID of the created user, or null if creation failed
     */
    String createUser(UserRequest request);

    /**
     * Creates an admin user with the specified attributes.
     * @param request User creation request containing username, password, first name, last name, email, and roles
     * @return the ID of the created admin user, or null if creation failed
     */
    String createAdminUser(UserRequest request);

    /**
     * Retrieves a user by their ID.
     * @param userId the ID of the user to retrieve
     * @return the UserRepresentation of the found user
     */
    UserRepresentation getUserById(String userId);
}
