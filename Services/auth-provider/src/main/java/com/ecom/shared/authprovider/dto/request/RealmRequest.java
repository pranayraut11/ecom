package com.ecom.shared.authprovider.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RealmRequest {

    @NotBlank(message = "Realm name is required")
    private String name;

    private boolean enabled = true;

    private String displayName;
}
