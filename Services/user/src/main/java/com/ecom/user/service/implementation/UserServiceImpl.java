package com.ecom.user.service.implementation;

import com.ecom.user.dto.AuthClientDetails;
import com.ecom.user.dto.KeycloakUser;
import com.ecom.user.dto.Login;
import com.ecom.user.dto.TokenDetails;
import com.ecom.user.entity.UserDetails;
import com.ecom.user.repository.UserRepository;
import com.ecom.user.rest.KeycloakAuthService;
import com.ecom.user.service.specification.UserService;
import com.ecom.user.utils.UserUtils;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class UserServiceImpl implements UserService {


    private KeycloakAuthService keycloakAuthService;

    private AuthClientDetails adminClientCredentials;

    private UserRepository userRepository;

    private AuthClientDetails userClientCredentials;

    @Value("${auth.realm}")
    private String realm;

    UserServiceImpl(KeycloakAuthService keycloakAuthService, @Qualifier("adminClientCredentials") AuthClientDetails adminClientCredentials,
                    UserRepository userRepository, AuthClientDetails userClientCredentials) {
        this.keycloakAuthService = keycloakAuthService;
        this.userRepository = userRepository;
        this.adminClientCredentials = adminClientCredentials;
        this.userClientCredentials = userClientCredentials;
    }

    @Override
    public void create(@NotNull UserDetails user)  {
        log.info("Creating user {} ... ",user.getEmail());
        TokenDetails tokenDetails = keycloakAuthService.login(adminClientCredentials, realm);
        KeycloakUser keycloakUser = new KeycloakUser();
        keycloakUser.setUsername(user.getEmail());
        keycloakUser.setFirstName(user.getFirstName());
        keycloakUser.setLastName(user.getLastName());
        keycloakUser.setCredentials(user.getCredentials());
        keycloakUser.setEnabled(user.isEnabled());
        keycloakUser.setEmail(user.getEmail());
        keycloakAuthService.createUser(keycloakUser, realm, tokenDetails.getAccess_token());
        userClientCredentials.setUsername(user.getUsername());
        userClientCredentials.setPassword(UserUtils.getPassword(user.getCredentials()));
        TokenDetails newlyCreatedUserToken = keycloakAuthService.login(userClientCredentials, realm);
        try {
            com.ecom.shared.common.dto.UserDetails.setUserInfo(newlyCreatedUserToken.getAccess_token());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        user.setUserId(com.ecom.shared.common.dto.UserDetails.getUserId());
        user.setCredentials(null);
        userRepository.save(user);
        log.info("User {} Created successfully!",user.getEmail());
    }

}