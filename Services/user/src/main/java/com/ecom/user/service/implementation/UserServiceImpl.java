package com.ecom.user.service.implementation;

import com.ecom.shared.exception.EcomException;
import com.ecom.shared.service.BaseService;
import com.ecom.user.dto.*;
import com.ecom.user.entity.UserDetails;
import com.ecom.user.model.Address;
import com.ecom.user.repository.UserRepository;
import com.ecom.user.rest.KeycloakAuthService;
import com.ecom.user.service.specification.UserService;
import com.ecom.user.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.common.VerificationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {


    private KeycloakAuthService keycloakAuthService;

    private AuthClientDetails adminCredentials;

    private Login masterUserCredentials;

    private UserRepository userRepository;

    private AuthClientDetails userClientCredentials;

    @Value("${user.auth.realms.subRealms}")
    private String subRealms;

    @Value("${user.auth.realms.master}")
    private String masterRealms;

    UserServiceImpl(KeycloakAuthService keycloakAuthService, @Qualifier("adminClientCredentials") AuthClientDetails adminCredentials,
                    Login masterUserCredentials, UserRepository userRepository, AuthClientDetails userClientCredentials) {
        this.keycloakAuthService = keycloakAuthService;
        this.masterUserCredentials = masterUserCredentials;
        this.userRepository = userRepository;
        this.adminCredentials = adminCredentials;
        this.userClientCredentials = userClientCredentials;
    }

    @Override
    public void create(@NotNull UserDetails user) throws VerificationException {
        log.info("Creating user {} ... ",user.getEmail());
        adminCredentials.setUsername(masterUserCredentials.getUsername());
        adminCredentials.setPassword(masterUserCredentials.getPassword());
        TokenDetails tokenDetails = keycloakAuthService.login(adminCredentials, masterRealms);
        KeycloakUser keycloakUser = new KeycloakUser();
        keycloakUser.setUsername(user.getEmail());
        keycloakUser.setFirstName(user.getFirstName());
        keycloakUser.setLastName(user.getLastName());
        keycloakUser.setCredentials(user.getCredentials());
        keycloakUser.setEnabled(user.isEnabled());
        keycloakUser.setEmail(user.getEmail());
        keycloakAuthService.createUser(keycloakUser, subRealms, tokenDetails.getAccess_token());
        userClientCredentials.setUsername(user.getUsername());
        userClientCredentials.setPassword(UserUtils.getPassword(user.getCredentials()));
        TokenDetails newlyCreatedUserToken = keycloakAuthService.login(userClientCredentials, subRealms);
        com.ecom.shared.dto.UserDetails.setUserInfo(newlyCreatedUserToken.getAccess_token());
        user.setUserId(com.ecom.shared.dto.UserDetails.getUserId());
        user.setCredentials(null);
        userRepository.save(user);
        log.info("User {} Created successfully!",user.getEmail());
    }

    @Override
    public void createOrUpdateAddress(@NotNull Address address) {
        String userId = com.ecom.shared.dto.UserDetails.getUserId();
        log.info("Saving address for user {} ... ",userId);
        UserDetails userDetails = userRepository.findByUserId(userId).orElseThrow(() -> new EcomException(HttpStatus.NOT_FOUND, "404"));
        if (StringUtils.isBlank(address.getId())) {
            address.setId(UUID.randomUUID().toString());
        }
        if (CollectionUtils.isEmpty(userDetails.getAddresses())) {
            userDetails.setAddresses(Collections.singleton(address));
        }else{
            userDetails.getAddresses().add(address);
        }
        log.info("Address saved/updated successfully for user {} ... ",userId);
    }
}