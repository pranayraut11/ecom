package com.ecom.authprovider.service.specification;

import com.ecom.authprovider.dto.request.UserCreateRequestDto;
import com.ecom.authprovider.dto.response.UserResponseDto;

/**
 * Service interface for user operations
 */
public interface UserService {

    /**
     * Creates a new user
     *
     * @param requestDto the user creation request data
     * @return response containing the created user details or error information
     */
    String createUser(UserCreateRequestDto requestDto);

    /**
     * Retrieves a user by ID
     *
     * @param userId the ID of the user to retrieve
     * @return response containing the user details or error information
     */
    UserResponseDto getUserById(String userId);
}
