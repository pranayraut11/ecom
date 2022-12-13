package com.ecom.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class AuthClientDetails {

    private String client_id;
    private String client_secret;
    private String grant_type;
    private String username;
    private String password;
}
