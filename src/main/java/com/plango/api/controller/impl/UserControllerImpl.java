package com.plango.api.controller.impl;

import com.plango.api.common.constant.ExceptionMessage;
import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.common.exception.UserAlreadyExistsException;
import com.plango.api.controller.UserController;

import com.plango.api.dto.travel.GetTravelDto;
import com.plango.api.dto.travel.UserTravelsDto;
import com.plango.api.dto.user.UserDto;
import com.plango.api.dto.user.UserUpdateDto;
import com.plango.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserControllerImpl implements UserController {

    @Autowired
    UserService userService;

    /**
     * Get information of the current user
     * @return user information, or else : user with id not found
     */
    @Override
    public ResponseEntity<UserDto> getCurrentUser() {
        try {
            UserDto userDto = userService.getCurrentUser();
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } catch (CurrentUserAuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
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
            return new ResponseEntity<>(ExceptionMessage.PSEUDO_EMAIL_TAKEN, HttpStatus.BAD_REQUEST);
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
        } catch (CurrentUserAuthorizationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
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
        } catch (CurrentUserAuthorizationException unfe) {
            return new ResponseEntity<>(unfe.getMessage(), HttpStatus.FORBIDDEN);
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
            List<GetTravelDto> travels = userService.getTravels();
            return new ResponseEntity<>(new UserTravelsDto(travels), HttpStatus.OK);
        }
        catch(CurrentUserAuthorizationException e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
