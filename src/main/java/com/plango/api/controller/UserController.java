package com.plango.api.controller;

import com.plango.api.entity.User;
import com.plango.api.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController("/user")
public class UserController {

    @Autowired
    UserService userService;


    @GetMapping(path = "/{id}")
    User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
