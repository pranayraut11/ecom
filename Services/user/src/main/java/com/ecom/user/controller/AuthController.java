package com.ecom.user.controller;

import com.ecom.user.dto.Login;
import com.ecom.user.dto.TokenDetails;
import com.ecom.user.service.specification.UserAuthService;
import org.keycloak.common.VerificationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("auth")
@CrossOrigin("*")
public class AuthController {

    private final UserAuthService userAuthService;

    AuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping("login")
    public ResponseEntity<TokenDetails> login(@RequestBody Login login) throws VerificationException {
        return ResponseEntity.ok(userAuthService.login(login));
    }

    @GetMapping("logout")
    public void logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        userAuthService.logout(token);
    }

    @GetMapping("exists")
    public boolean exists(@RequestParam String email) {
        return userAuthService.exists(email);
    }

}
