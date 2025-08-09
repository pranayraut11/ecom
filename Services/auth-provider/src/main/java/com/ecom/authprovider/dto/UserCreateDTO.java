package com.ecom.authprovider.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;
@Data
public class UserCreateDTO {
    private String id;
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Map<String, List<String>> attributes;
}
