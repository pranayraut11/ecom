package com.ecom.user.controller;

import com.ecom.user.dto.User;
import com.ecom.user.entity.UserMongo;
import com.ecom.user.service.specification.UserService;
import org.ecom.shared.controller.BaseController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
public class UserController extends BaseController<User> {

    UserService userService;

    UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/addUser")
    public void addUser(@RequestBody UserMongo user){
        userService.create(user);
    }
}
