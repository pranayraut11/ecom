package com.ecom.authprovider.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating a new realm")
public class RealmCreateRequest {
    
    @NotBlank(message = "Realm name is required")
    @Schema(description = "Name of the realm to create", example = "my-company")
    private String name;
    
    @Schema(description = "Display name for the realm", example = "My Company")
    private String displayName;
    
    @Schema(description = "Whether the realm should be enabled", example = "true", defaultValue = "true")
    private Boolean enabled = true;
}
