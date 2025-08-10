package com.ecom.user.service.implementation;

import com.ecom.user.dto.*;
import com.ecom.user.entity.UserDetails;
import com.ecom.user.mapper.UserMapper;
import com.ecom.user.repository.UserRepository;
import com.ecom.user.rest.AuthRestService;
import com.ecom.user.service.specification.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final AuthRestService keycloakAuthService;
    private final UserRepository userRepository;
    private final AuthClientDetails userClientCredentials;
    private final UserMapper userMapper;

    @Value("${auth-server.realm}")
    private String realm;

    UserServiceImpl(AuthRestService keycloakAuthService,
                   @Qualifier("adminClientCredentials") AuthClientDetails adminClientCredentials,
                   UserRepository userRepository,
                   AuthClientDetails userClientCredentials,
                   UserMapper userMapper) {
        this.keycloakAuthService = keycloakAuthService;
        this.userRepository = userRepository;
        this.userClientCredentials = userClientCredentials;
        this.userMapper = userMapper;
    }

    @Override
    public void create(@NotNull UserCreationDTO userDTO) {
        log.info("Creating user {} ... ", userDTO.getEmail());

        // Convert DTO to entity using mapper
        UserDetails user = userMapper.toEntity(userDTO);

        // Convert to KeycloakUser using mapper
        KeycloakUser keycloakUser = userMapper.toKeycloakUser(userDTO);

        // Get the user ID from the createUser call
        String userId = keycloakAuthService.createUser(keycloakUser);

        // Set the user ID from Keycloak response
        if (userId != null && !userId.isEmpty()) {
            user.setUserId(userId);
            log.info("Set user ID from Keycloak: {}", userId);
        } else {
            log.warn("Failed to get user ID from Keycloak, using default ID");
            user.setUserId(com.ecom.shared.contract.dto.UserDetails.getUserId());
        }

        userClientCredentials.setUsername(user.getEmail());
        user.setCredentials(null);
        userRepository.save(user);
        log.info("User {} Created successfully!", user.getEmail());
    }

    @Override
    public User getUserDetails() {
        return null;
    }

}