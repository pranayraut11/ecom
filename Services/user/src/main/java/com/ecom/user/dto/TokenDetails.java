package com.ecom.user.dto;

import lombok.Data;
import java.util.Set;

@Data
public class TokenDetails {

    private String access_token;
    private int expires_in;
    private int refresh_expires_in;
    private String refresh_token;
    private String token_type;
    private String session_state;
    private String scope;
    private Set<String> roles;
    private String name;
    private String tenantId;
}
