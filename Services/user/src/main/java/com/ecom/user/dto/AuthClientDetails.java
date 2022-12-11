package com.ecom.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
public class AuthClientDetails extends Login{

    private String client_id;
    private String client_secret;
    private String grant_type;
}
