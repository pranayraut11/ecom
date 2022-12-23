package com.ecom.user.service.specification;

import com.ecom.user.entity.UserMongo;
import org.keycloak.common.VerificationException;
import org.springframework.stereotype.Service;

@Service
public interface UserService{

    public void create(UserMongo user) throws VerificationException;
}
