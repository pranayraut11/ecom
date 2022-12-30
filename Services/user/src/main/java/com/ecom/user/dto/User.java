package com.ecom.user.dto;

import com.ecom.user.model.Credential;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public abstract class User {

    private String firstName;
    private String lastName;

    private String username;

    private List<Credential> credentials;

    private String email;



    private boolean enabled;

   // private List<Address> addresses;

}