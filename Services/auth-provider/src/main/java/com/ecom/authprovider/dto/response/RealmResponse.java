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
@Schema(description = "Response object after creating a realm")
public class RealmResponse {
    
    @Schema(description = "ID of the created realm")
    private String id;
    
    @Schema(description = "Name of the realm")
    private String name;
    
    @Schema(description = "Display name of the realm")
    private String displayName;
    
    @Schema(description = "Whether the realm is enabled")
    private Boolean enabled;
    
    @Schema(description = "Creation status message")
    private String message;
}
