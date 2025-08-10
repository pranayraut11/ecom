package com.ecom.user.dto;

import com.ecom.user.model.Address;
import com.ecom.user.model.Credential;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * DTO for user creation requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private boolean emailVerified;
    private boolean enabled;
    private List<Credential> credentials;
    private String phoneNumber;
    private boolean phoneNumberVerified;
    private LocalDate dateOfBirth;
    private String gender;
    private String profileImageUrl;
    private Set<Address> addresses;
}
