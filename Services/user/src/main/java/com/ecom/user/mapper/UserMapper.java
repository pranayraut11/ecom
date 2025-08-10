package com.ecom.user.mapper;

import com.ecom.user.dto.KeycloakUser;
import com.ecom.user.dto.UserCreationDTO;
import com.ecom.user.entity.UserDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between User DTOs and entities
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Convert from UserCreationDTO to UserDetails entity
     */
    UserDetails toEntity(UserCreationDTO userCreationDTO);

    /**
     * Convert from UserDetails entity to UserCreationDTO
     */
    UserCreationDTO toDto(UserDetails userDetails);

    /**
     * Convert from UserDetails to KeycloakUser
     */
    @Mapping(target = "username", source = "email")
    KeycloakUser toKeycloakUser(UserDetails userDetails);

    /**
     * Convert from UserCreationDTO to KeycloakUser
     */
    @Mapping(target = "username", source = "email")
    KeycloakUser toKeycloakUser(UserCreationDTO userCreationDTO);
}
