package com.plango.api.controller;

import com.plango.api.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface UserController {

    @PostMapping(path = "", consumes="application/json")
    ResponseEntity<String> createUser(@RequestBody User user);

    @PutMapping(path = "/{id}")
    ResponseEntity<String> putUser(@PathVariable Long id, @RequestBody User user);

    @GetMapping(path = "/{id}")
    User getUserById(@PathVariable Long id);
}
