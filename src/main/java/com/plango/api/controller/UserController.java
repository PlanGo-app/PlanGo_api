package com.plango.api.controller;

import com.plango.api.dto.UserDto;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
public interface UserController {

    @GetMapping(path = "/{id}")
    User getUserById(@PathVariable Long id) throws UserNotFoundException;

    @PostMapping(path = "", consumes="application/json")
    ResponseEntity<String> createUser(@RequestBody User user);

    @PutMapping(path = "/{id}")
    ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody UserDto userDto);

    @DeleteMapping(path = "/delete/{id}")
    ResponseEntity<String> deleteUser(@PathVariable Long id);
}
