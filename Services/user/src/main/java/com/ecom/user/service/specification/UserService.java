package com.ecom.user.service.specification;

import com.ecom.user.entity.UserDetails;
import com.ecom.user.model.Address;
import org.keycloak.common.VerificationException;
import org.springframework.stereotype.Service;

@Service
public interface UserService{

    public void create(UserDetails user) throws VerificationException;

    public void createOrUpdateAddress(Address address);
}
