package com.ecom.authprovider.mapper;

import com.ecom.authprovider.dto.request.UserCreateRequestDto;
import com.ecom.authprovider.dto.request.UserRequest;
import com.ecom.authprovider.dto.response.UserResponseDto;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for converting between User DTOs and Keycloak UserRepresentation
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Maps a UserCreateRequestDto to a Keycloak UserRepresentation
     *
     * @param requestDto the user creation request DTO
     * @return the Keycloak UserRepresentation
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attributes", ignore = true)
    @Mapping(target = "access", ignore = true)
    @Mapping(target = "clientConsents", ignore = true)
    @Mapping(target = "clientRoles", ignore = true)
    @Mapping(target = "credentials", ignore = true)
    @Mapping(target = "disableableCredentialTypes", ignore = true)
    @Mapping(target = "federatedIdentities", ignore = true)
    @Mapping(target = "federationLink", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "notBefore", ignore = true)
    @Mapping(target = "origin", ignore = true)
    @Mapping(target = "realmRoles", ignore = true)
    @Mapping(target = "requiredActions", ignore = true)
    @Mapping(target = "self", ignore = true)
    @Mapping(target = "serviceAccountClientId", ignore = true)
    @Mapping(target = "socialLinks", ignore = true)
    @Mapping(target = "totp", ignore = true)
    @Mapping(source = "enabled", target = "enabled", defaultValue = "true")
    @Mapping(source = "emailVerified", target = "emailVerified", defaultValue = "true")
    UserRepresentation toUserRepresentation(UserCreateRequestDto requestDto);

    /**
     * Maps a UserCreateRequestDto to a UserRequest
     *
     * @param requestDto the user creation request DTO
     * @return the UserRequest object for the manager layer
     */
    UserRequest toUserRequest(UserCreateRequestDto requestDto);

    /**
     * Maps a Keycloak UserRepresentation to a UserResponseDto
     *
     * @param userRepresentation the Keycloak UserRepresentation
     * @return the user response DTO
     */
    UserResponseDto toUserResponseDto(UserRepresentation userRepresentation);

    /**
     * Creates a success response with a user ID and message
     *
     * @param userId the user ID
     * @param message the success message
     * @return the user response DTO
     */
    @Named("createSuccessResponse")
    default UserResponseDto createSuccessResponse(String userId, String message) {
        return new UserResponseDto(
                userId,
                null,
                null,
                null,
                null,
                null,
                null,
                message
        );
    }
}
