package com.ecom.user.service.specification;

import com.ecom.user.dto.User;
import com.ecom.user.entity.UserDetails;
import org.keycloak.common.VerificationException;
import org.springframework.stereotype.Service;

@Service
public interface UserService{

    void create(UserDetails user) throws VerificationException;
    User getUserDetails();
}
