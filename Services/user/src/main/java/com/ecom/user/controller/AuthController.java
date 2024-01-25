package com.ecom.user.controller;

import com.ecom.user.dto.Login;
import com.ecom.user.dto.TokenDetails;
import com.ecom.user.service.specification.AuthService;
import org.keycloak.common.VerificationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("auth")
@CrossOrigin("*")
public class AuthController {

    private AuthService authService;

    AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("login")
    public ResponseEntity<TokenDetails> login(@RequestBody Login login) throws VerificationException {
        return ResponseEntity.ok(authService.login(login));
    }

    @GetMapping("logout")
    public void logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        authService.logout(token);
    }
}
