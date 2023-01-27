package com.ecom.user.controller;

import com.ecom.shared.controller.BaseController;
import com.ecom.user.dto.User;
import com.ecom.user.entity.UserDetails;
import com.ecom.user.service.specification.UserService;
import org.keycloak.common.VerificationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
public class UserController  {

    UserService userService;

    UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/addUser")
    public void addUser(@RequestBody UserDetails user) throws VerificationException {
        userService.create(user);
    }
}
