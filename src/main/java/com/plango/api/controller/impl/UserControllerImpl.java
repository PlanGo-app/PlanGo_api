package com.plango.api.controller.impl;

import com.plango.api.common.exception.UserAlreadyExistsException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.controller.UserController;
import com.plango.api.dto.UserDto;
import com.plango.api.entity.User;
import com.plango.api.security.JwtChecker;
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

    @Autowired
    JwtChecker jwt;

    @Override
    public UserDto getUserById(Long id) throws UserNotFoundException {
        return convertToDto(userService.getUserById(id));
    }

    @Override
    public ResponseEntity<String> createUser(UserDto userDto) {
        try {
            userService.createUser(convertToEntity(userDto));
        }
        catch(UserAlreadyExistsException e){
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
        }
    }

    @Override
    public ResponseEntity<String> deleteUser(String header, Long id) {
        try {
            String token = jwt.getToken(header);
            String username = jwt.getUsernameWithValidToken(token);
            userService.deleteUser(id);
        } catch (UserNotFoundException unfe) {
            return new ResponseEntity<>(unfe.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(String.format("User %d deleted", id), HttpStatus.OK);
    }

    private UserDto convertToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    private User convertToEntity(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(encoder.encode(userDto.getPassword()));
        return user;
    }
}
