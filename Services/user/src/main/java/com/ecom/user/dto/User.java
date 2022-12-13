package com.ecom.user.dto;

import com.ecom.user.model.Address;
import com.ecom.user.model.Credential;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ecom.shared.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
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