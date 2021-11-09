package com.plango.api.controller.impl;

import com.plango.api.controller.UserController;
import com.plango.api.entity.User;
import com.plango.api.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserControllerImpl implements UserController {

    @Autowired
    UserService userService;


    public ResponseEntity<String> createUser(@RequestBody User user) {
        userService.addOrUpdateUser(user);
        return new ResponseEntity<>(String.format("User %s created", user.getPseudo()), HttpStatus.CREATED);
    }

    public ResponseEntity<String> putUser(@PathVariable Long id,@RequestBody User user) {
        User userFind = userService.getUserById(id);
        if(userFind == null){
            return new ResponseEntity<>(String.format("User %d doesn't exist", id), HttpStatus.BAD_REQUEST);
        }
        userFind.setEmail(user.getEmail());
        userFind.setPassword(user.getPassword());
        userService.addOrUpdateUser(userFind);
        return new ResponseEntity<>(String.format("User %d updated", id), HttpStatus.OK);
    }

    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
