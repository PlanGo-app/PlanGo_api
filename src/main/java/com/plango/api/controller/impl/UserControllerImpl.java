package com.plango.api.controller.impl;

import com.plango.api.common.exception.UserAlreadyExistsException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.controller.UserController;
import com.plango.api.dto.*;
import com.plango.api.entity.Travel;

import com.plango.api.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserControllerImpl implements UserController {

    @Autowired
    UserService userService;

    @Autowired
    ModelMapper modelMapper;

    /**
     * Get information of the current user
     * @return user information, or else : user with id not found
     */
    @Override
    public ResponseEntity<UserDto> getCurrentUser() {
        try {
            UserDto userDto = userService.getCurrentUser();
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
            userService.createUser(userDto);
        } catch(UserAlreadyExistsException e){
            return new ResponseEntity<>("Pseudo or email already taken.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(String.format("User %s created", userDto.getPseudo()), HttpStatus.CREATED);
    }

    /**
     * Update current user
     * @param userUpdateDto new user's information
     * @return validation of update, or else : exception message when problem with user authenticated
     */
    @Override
    public ResponseEntity<String> updateCurrentUser(UserUpdateDto userUpdateDto) {
        try {
            userService.updateUser(userUpdateDto);
            return new ResponseEntity<>("Current user updated", HttpStatus.OK);
        } catch (UserNotFoundException unfe) {
            return new ResponseEntity<>(unfe.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete the current user
     * @return validation of deleting, or else : exception message when problem with user authenticated
     */
    @Override
    public ResponseEntity<String> deleteCurrentUser() {
        try {
            userService.deleteCurrentUser();
        } catch (UserNotFoundException unfe) {
            return new ResponseEntity<>(unfe.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Current user deleted", HttpStatus.OK);
    }

    /**
     * Send all user's travels information
     * @return List<TravelDto> travels information, or else : user authenticated not found
     */
    @Override
    public ResponseEntity<UserTravelsDto> getTravelsOfCurrentUser() {
        try {
            List<Travel> travelsOfUser = userService.getTravels();
            List<TravelDto> travels = travelsOfUser.stream().map(travel -> modelMapper.map(travel, TravelDto.class)).collect(Collectors.toList());
            return new ResponseEntity<>(new UserTravelsDto(travels), HttpStatus.OK);
        }
        catch(UserNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
