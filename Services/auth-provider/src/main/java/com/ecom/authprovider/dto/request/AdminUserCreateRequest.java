package com.ecom.authprovider.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating an admin user in a realm")
public class AdminUserCreateRequest {

    @NotBlank(message = "Username is required")
    @Schema(description = "Username for the admin user", example = "admin.user")
    private String username;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must contain at least 8 characters including one uppercase letter, one lowercase letter, one number and one special character")
    @Schema(description = "Password for the admin user", example = "Admin@123")
    private String password;

    @NotBlank(message = "Email is required")
    @Schema(description = "Email for the admin user", example = "admin@example.com")
    private String email;

    @Schema(description = "First name of the admin user", example = "Admin")
    private String firstName;

    @Schema(description = "Last name of the admin user", example = "User")
    private String lastName;

    @Schema(description = "Whether the user should be enabled", example = "true", defaultValue = "true")
    private Boolean enabled = true;
}
