package com.ecom.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class Login {

    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
}
