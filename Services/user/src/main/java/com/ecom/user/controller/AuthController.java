package com.ecom.user.controller;

import com.ecom.user.dto.Login;
import com.ecom.user.dto.TokenDetails;
import com.ecom.user.rest.KeycloakAuthService;
import com.ecom.user.service.specification.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {

    private AuthService authService;

    AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("login")
    public ResponseEntity<TokenDetails> login(@RequestBody Login login){
        return ResponseEntity.ok(authService.login(login));
    }
}
