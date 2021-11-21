package com.plango.api.controller.impl;

import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.common.exception.UserAlreadyExistsException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.controller.UserController;
import com.plango.api.dto.*;
import com.plango.api.entity.Travel;
import com.plango.api.entity.User;

import com.plango.api.service.TravelService;
import com.plango.api.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserControllerImpl implements UserController {

    @Autowired
    UserService userService;

    @Autowired
    TravelService travelService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    PasswordEncoder encoder;

    /**
     * Send user information
     * @param id : user's id to find
     * @return user information, or else : user with id not found
     */
    @Override
    public ResponseEntity<UserDto> getUserById(Long id) {
        try {
            UserDto userDto = convertToDto(userService.getUserById(id));
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } catch (UserNotFoundException unfe) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Create a new user
     * @param userDto user's information
     * @return CREATED, or else : exception message when user already exist
     */
    @Override
    public ResponseEntity<String> createUser(UserDto userDto) {
        try {
            userService.createUser(convertToEntity(userDto));
        } catch(UserAlreadyExistsException e){
            return new ResponseEntity<>("Pseudo or email already taken.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(String.format("User %s created", userDto.getPseudo()), HttpStatus.CREATED);
    }

    /**
     * Update a user
     * @param id user's id
     * @param userUpdateDto new user's information
     * @return validation of update, or else : exception message when problem with user authenticated
     */
    @Override
    public ResponseEntity<String> updateUser(Long id, UserUpdateDto userUpdateDto) {
        try {
            userService.updateUser(id, convertToEntity(userUpdateDto));
            return new ResponseEntity<>(String.format("User %d updated", id), HttpStatus.OK);
        } catch (UserNotFoundException unfe) {
            return new ResponseEntity<>(unfe.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (CurrentUserAuthorizationException cuie) {
            return new ResponseEntity<>(cuie.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Delete a user
     * @param id user's id to delete
     * @return validation of deleting, or else : exception message when problem with user authenticated
     */
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

    /**
     * Send all user's travels information
     * @return List<TravelDto> travels information, or else : user authenticated not found
     */
    @Override
    public ResponseEntity<UserTravelsDto> getTravelsByUser(Long id) {
        try {
            List<Travel> travelsOfUser = travelService.getTravelsOfCurrentUser(id);
            List<TravelDto> travels = travelsOfUser.stream().map(travel -> modelMapper.map(travel, TravelDto.class)).collect(Collectors.toList());
            return new ResponseEntity<>(new UserTravelsDto(travels), HttpStatus.OK);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private UserDto convertToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    private User convertToEntity(UserBaseDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        if (user.getPassword() != null) {
            user.setPassword(encoder.encode(userDto.getPassword()));
        }
        return user;
    }
}
