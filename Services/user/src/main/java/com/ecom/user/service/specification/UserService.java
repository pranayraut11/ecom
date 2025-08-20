package com.ecom.user.service.specification;

import com.ecom.user.dto.User;
import com.ecom.user.dto.UserCreationDTO;
import org.keycloak.common.VerificationException;
import org.springframework.stereotype.Service;

@Service
public interface UserService{

    void create(UserCreationDTO userDTO) throws VerificationException;
    User getUserDetails();
    void deleteUser();
}
