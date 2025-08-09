package com.ecom.authprovider.service.implementation;

import com.ecom.authprovider.dto.request.UserCreateRequestDto;
import com.ecom.authprovider.dto.request.UserRequest;
import com.ecom.authprovider.dto.response.UserResponseDto;
import com.ecom.authprovider.exception.KeycloakServiceException;
import com.ecom.authprovider.manager.api.UserManager;
import com.ecom.authprovider.mapper.UserMapper;
import com.ecom.authprovider.service.specification.UserService;
import com.ecom.shared.common.config.common.TenantContext;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

/**
 * Implementation of UserService for Keycloak user operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserManager userManager;
    private final UserMapper userMapper;

    /**
     * Creates a new user in Keycloak
     *
     * @param requestDto the user creation request data
     * @return response containing the created user details or error information
     */
    @Override
    public UserResponseDto createUser(UserCreateRequestDto requestDto) {
        log.info("Creating user with username: {}", requestDto.username());

        try {
            // Validate that tenant context is set
            if (TenantContext.getTenantId() == null || TenantContext.getTenantId().isEmpty()) {
                throw new IllegalStateException("Tenant ID is not set in TenantContext");
            }

            // Convert DTO to UserRequest using mapper
            UserRequest userRequest = userMapper.toUserRequest(requestDto);

            // Create user using manager
            String userId = userManager.createUser(userRequest);

            if (userId == null || userId.isEmpty()) {
                String errorMessage = String.format("Failed to create user '%s'", requestDto.username());
                log.error(errorMessage);
                return UserResponseDto.error(errorMessage);
            }

            // Return success response
            return userMapper.createSuccessResponse(userId,
                    String.format("User '%s' created successfully", requestDto.username()));

        } catch (IllegalArgumentException e) {
            log.error("Invalid user request: {}", e.getMessage());
            return UserResponseDto.error("Invalid user request: " + e.getMessage());
        } catch (KeycloakServiceException e) {
            // Already formatted exception, just return error response
            log.error("Keycloak service error: {}", e.getMessage());
            return UserResponseDto.error(e.getMessage());
        } catch (NotFoundException e) {
            String errorMessage = String.format("Realm not found for user '%s'", requestDto.username());
            log.error(errorMessage, e);
            return UserResponseDto.error(errorMessage);
        } catch (Exception e) {
            String errorMessage = String.format("Error creating user '%s': %s",
                    requestDto.username(), e.getMessage());
            log.error(errorMessage, e);
            return UserResponseDto.error(errorMessage);
        }
    }

    /**
     * Retrieves a user by ID from Keycloak
     *
     * @param userId the ID of the user to retrieve
     * @return response containing the user details or error information
     */
    @Override
    public UserResponseDto getUserById(String userId) {
        log.info("Retrieving user with ID: {}", userId);

        try {
            // Validate that tenant context is set
            if (TenantContext.getTenantId() == null || TenantContext.getTenantId().isEmpty()) {
                throw new IllegalStateException("Tenant ID is not set in TenantContext");
            }

            // Get user from Keycloak
            UserRepresentation userRepresentation = userManager.getUserById(userId);

            // Convert to response DTO
            return userMapper.toUserResponseDto(userRepresentation);

        } catch (NotFoundException e) {
            String errorMessage = String.format("User with ID '%s' not found", userId);
            log.error(errorMessage, e);
            return UserResponseDto.error(errorMessage);
        } catch (Exception e) {
            String errorMessage = String.format("Error retrieving user with ID '%s': %s",
                    userId, e.getMessage());
            log.error(errorMessage, e);
            return UserResponseDto.error(errorMessage);
        }
    }
}
