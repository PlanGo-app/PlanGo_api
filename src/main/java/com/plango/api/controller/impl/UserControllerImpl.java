package com.plango.api.controller.impl;

import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.controller.UserController;
import com.plango.api.dto.UserDto;
import com.plango.api.entity.User;
import com.plango.api.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserControllerImpl implements UserController {

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder encoder;

    public User getUserById(@PathVariable Long id) throws UserNotFoundException {
        return userService.getUserById(id);
    }

    public ResponseEntity<String> createUser(@RequestBody User user) {
        userService.createUser(user);
        //
        try {
            String pwd = user.getPassword();
            user.setPassword(encoder.encode(pwd));
            userService.addUser(user);
        }
        catch(Exception e){
            return new ResponseEntity<>("Pseudo or email already taken.", HttpStatus.BAD_REQUEST);
        }
        //
        return new ResponseEntity<>(String.format("User %s created", user.getPseudo()), HttpStatus.CREATED);
    }

    public ResponseEntity<String> updateUser(@PathVariable Long id,@RequestBody User user) {
        try {
            userService.updateUser(id, user);
        } catch (UserNotFoundException une) {
            return new ResponseEntity<>(une.getMessage(), HttpStatus.BAD_REQUEST);
        }
        //
        userFind.setEmail(userDto.getEmail());
        userFind.setPassword(encoder.encode(userDto.getPassword()));
        userService.updateUser(userFind);
        return new ResponseEntity<>(String.format("User %d updated", id), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> deleteUser(Long id) {
        try {
            userService.deleteUser(id);
        } catch (UserNotFoundException une) {
            return new ResponseEntity<>(une.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(String.format("User %d deleted", id), HttpStatus.OK);
    }
}
