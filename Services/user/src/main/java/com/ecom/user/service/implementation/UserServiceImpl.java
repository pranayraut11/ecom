package com.ecom.user.service.implementation;

import com.ecom.user.dto.*;
import com.ecom.user.entity.UserDetails;
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


    private AuthRestService keycloakAuthService;

    private AuthClientDetails adminClientCredentials;

    private UserRepository userRepository;

    private AuthClientDetails userClientCredentials;

    @Value("${auth-server.realm}")
    private String realm;

    UserServiceImpl(AuthRestService keycloakAuthService, @Qualifier("adminClientCredentials") AuthClientDetails adminClientCredentials,
                    UserRepository userRepository, AuthClientDetails userClientCredentials) {
        this.keycloakAuthService = keycloakAuthService;
        this.userRepository = userRepository;
        this.adminClientCredentials = adminClientCredentials;
        this.userClientCredentials = userClientCredentials;
    }

    @Override
    public void create(@NotNull UserDetails user)  {
        log.info("Creating user {} ... ",user.getEmail());
       // TokenDetails tokenDetails = keycloakAuthService.login(adminClientCredentials, realm);
        KeycloakUser keycloakUser = new KeycloakUser();
        keycloakUser.setFirstName(user.getFirstName());
        keycloakUser.setLastName(user.getLastName());
        keycloakUser.setCredentials(user.getCredentials());
        keycloakUser.setEnabled(user.isEnabled());
        keycloakUser.setEmail(user.getEmail());
        keycloakUser.setPassword(user.getPassword());
        keycloakUser.setUsername(user.getEmail());

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
        log.info("User {} Created successfully!",user.getEmail());
    }

    @Override
    public User getUserDetails() {
        return null;
    }

}