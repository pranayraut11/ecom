package com.ecom.tenantmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating tenant information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantUpdateRequest {

    @Size(min = 2, max = 100, message = "Tenant name must be between 2 and 100 characters")
    private String tenantName;

    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]\\.[a-zA-Z]{2,}$",
             message = "Domain must be a valid domain format")
    @Size(max = 255, message = "Domain must not exceed 255 characters")
    private String domain;

    @Email(message = "Contact email must be a valid email address")
    @Size(max = 255, message = "Contact email must not exceed 255 characters")
    private String contactEmail;
}
