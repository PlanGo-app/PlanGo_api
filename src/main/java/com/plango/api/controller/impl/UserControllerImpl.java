package com.plango.api.controller.impl;

import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.common.exception.UserAlreadyExistsException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.controller.UserController;
import com.plango.api.dto.UserDto;
import com.plango.api.entity.User;
import com.plango.api.service.UserService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserControllerImpl implements UserController {

    @Autowired
    UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public ResponseEntity<UserDto> getUserById(Long id) {
        try {
            UserDto userDto = convertToDto(userService.getUserById(id));
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } catch (UserNotFoundException unfe) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<String> createUser(UserDto userDto) {
        try {
            userService.createUser(convertToEntity(userDto));
        } catch(UserAlreadyExistsException e){
            return new ResponseEntity<>("Pseudo or email already taken.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(String.format("User %s created", userDto.getPseudo()), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<String> updateUser(Long id, UserDto userDto) {
        try {
            userService.updateUser(convertToEntity(userDto));
            return new ResponseEntity<>(String.format("User %d updated", id), HttpStatus.OK);
        } catch (UserNotFoundException unfe) {
            return new ResponseEntity<>(unfe.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (CurrentUserAuthorizationException cuie) {
            return new ResponseEntity<>(cuie.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<String> deleteUser(Long id) {
        try {
            userService.deleteUser(id);
        } catch (UserNotFoundException unfe) {
            return new ResponseEntity<>(unfe.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (CurrentUserAuthorizationException cuie) {
            return new ResponseEntity<>(cuie.getMessage(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(String.format("User %d deleted", id), HttpStatus.OK);
    }

    private UserDto convertToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    private User convertToEntity(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        if (user.getPassword() != null) {
            user.setPassword(encoder.encode(userDto.getPassword()));
        }
        return user;
    }
}
