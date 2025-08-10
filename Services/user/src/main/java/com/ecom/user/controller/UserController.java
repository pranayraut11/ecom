package com.ecom.user.controller;

import com.ecom.user.dto.UserCreationDTO;
import com.ecom.user.service.specification.UserService;
import org.keycloak.common.VerificationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController  {

    private final UserService userService;

    UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping()
    public void addUser(@RequestBody UserCreationDTO userDTO) throws VerificationException {
        userService.create(userDTO);
    }

}
