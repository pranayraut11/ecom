package com.ecom.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class Login {

    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
}
