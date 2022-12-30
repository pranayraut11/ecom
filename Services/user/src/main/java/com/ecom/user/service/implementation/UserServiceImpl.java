package com.ecom.user.service.implementation;

import com.ecom.shared.service.BaseService;
import com.ecom.user.dto.*;
import com.ecom.user.dto.User;
import com.ecom.user.entity.UserMongo;
import com.ecom.user.repository.UserRepository;
import com.ecom.user.rest.KeycloakAuthService;
import com.ecom.user.service.specification.UserService;
import org.keycloak.common.VerificationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends BaseService<User> implements UserService {


    private KeycloakAuthService keycloakAuthService;

    private AuthClientDetails adminCredentials;

    private Login masterUserCredentials;

    private UserRepository userRepository;

    @Value("${user.auth.realms.subRealms}")
    private String subRealms;

    @Value("${user.auth.realms.master}")
    private String masterRealms;

    UserServiceImpl(KeycloakAuthService keycloakAuthService, @Qualifier("adminClientCredentials") AuthClientDetails adminCredentials,  Login masterUserCredentials, UserRepository userRepository){
        this.keycloakAuthService = keycloakAuthService;
        this.masterUserCredentials = masterUserCredentials;
        this.userRepository = userRepository;
        this.adminCredentials = adminCredentials;
    }

    @Override
    public List<User> getAll() {
        return null;
    }

    @Override
    public User get(String id) {
        return null;
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public User create(User user) {

        return null;
    }

    @Override
    public User update(User entity) {
        return null;
    }

    @Override
    public void create(UserMongo user) throws VerificationException {
        adminCredentials.setUsername(masterUserCredentials.getUsername());
        adminCredentials.setPassword(masterUserCredentials.getPassword());
        TokenDetails tokenDetails = keycloakAuthService.login(adminCredentials,masterRealms);
        KeycloakUser keycloakUser = new KeycloakUser();
        keycloakUser.setUsername(user.getEmail());
        keycloakUser.setFirstName(user.getFirstName());
        keycloakUser.setLastName(user.getLastName());
        keycloakUser.setCredentials(user.getCredentials());
        keycloakUser.setEnabled(user.isEnabled());
        keycloakUser.setEmail(user.getEmail());
        keycloakAuthService.createUser(keycloakUser,subRealms,tokenDetails.getAccess_token());
        user.setCredentials(null);
        userRepository.save(user);
    }
}