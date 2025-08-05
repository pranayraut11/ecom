package com.ecom.user.dto;

import com.ecom.user.model.Credential;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDate;
import java.util.List;


@Data
@NoArgsConstructor
public abstract class User {

    private String firstName;
    private String lastName;
    @Indexed(unique = true)
    private String email;
    private boolean emailVerified;
    private boolean enabled;
    private List<Credential> credentials;

    private String phoneNumber;
    private boolean phoneNumberVerified;
    private LocalDate dateOfBirth;
    private String gender;
    private String profileImageUrl;

}