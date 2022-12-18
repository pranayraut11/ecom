package com.ecom.user.service.specification;

import com.ecom.user.dto.Login;
import com.ecom.user.dto.TokenDetails;

public interface AuthService {
    public TokenDetails login(Login login);

    public void logout(String token);

}
