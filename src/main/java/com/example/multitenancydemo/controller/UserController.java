package com.example.multitenancydemo.controller;

import com.example.multitenancydemo.entity.User;
import com.example.multitenancydemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/create")
    public User create(@RequestBody User user) {
        return this.userService.create(user);
    }
}
