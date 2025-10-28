package com.ecom.authprovider.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object after creating an admin user")
public class AdminUserResponse {
    
    @Schema(description = "ID of the created user")
    private String id;
    
    @Schema(description = "Username of the created user")
    private String username;
    
    @Schema(description = "Email of the created user")
    private String email;
    
    @Schema(description = "Whether the user is enabled")
    private Boolean enabled;
    
    @Schema(description = "Creation status message")
    private String message;
}
