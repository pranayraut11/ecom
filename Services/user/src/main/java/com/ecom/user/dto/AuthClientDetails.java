package com.ecom.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class AuthClientDetails {

    private String clientId;
    private String clientSecret;
    private String grantType;
    private String username;
    private String password;
}
