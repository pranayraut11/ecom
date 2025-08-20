package com.ecom.user.controller;

import com.ecom.user.dto.ApiGenericResponse;
import com.ecom.user.dto.UserCreationDTO;
import com.ecom.user.service.specification.UserService;
import jakarta.validation.Valid;
import org.keycloak.common.VerificationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("users")
@CrossOrigin("*")
public class UserController  {

    private final UserService userService;

    UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity<ApiGenericResponse<String>> addUser(@RequestBody @Valid UserCreationDTO userDTO) throws VerificationException {
        userService.create(userDTO);
        return ResponseEntity.created(URI.create("user")).body(ApiGenericResponse.success("User created successfully"));
    }

    @DeleteMapping()
    public ResponseEntity<ApiGenericResponse<String>> deleteUser()  {
        return ResponseEntity.created(URI.create("user")).body(ApiGenericResponse.success("User created successfully"));
    }

}
