package com.ecom.user.dto;

import com.ecom.user.model.Address;
import com.ecom.user.model.Credential;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 50, message = "First name must be between 3 and 50 characters")
    private String firstName;
    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 50, message = "Last name must be between 3 and 50 characters")
    private String lastName;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;
    private boolean emailVerified;
    private boolean enabled;
    @NotBlank(message = "Mobile is required")
    @Size(min = 10, max = 10, message = "Mobile must be 10 characters")
    private String mobile;
    private boolean phoneNumberVerified;
    private LocalDate dateOfBirth;
    private String gender;
    private String profileImageUrl;
    private Set<Address> addresses;
}
