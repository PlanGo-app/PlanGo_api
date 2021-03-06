package com.plango.api.controller.impl;

import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.controller.AuthController;
import com.plango.api.dto.authentication.AuthDto;
import com.plango.api.dto.authentication.CredentialDto;
import com.plango.api.dto.user.UserDto;
import com.plango.api.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthControllerImpl implements AuthController {

    @Autowired
    AuthService authService;

    /**
     * User log in the application
     * @param credentials pseudo and password' user
     * @return : token, or else : exception message
     */
    public ResponseEntity<AuthDto> login(@RequestBody CredentialDto credentials) {

    	try {
            AuthDto authDto = authService.getLogin(credentials);
            return new ResponseEntity<>(authDto, HttpStatus.OK);
    	}
    	catch (AuthenticationException | CurrentUserAuthorizationException e) {
    		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    	}
        
    }

    /**
     * New user sign up to the application
     * @param userDto all new user essential information
     * @return : token, or else : exception message
     */
    public ResponseEntity<AuthDto> signup(@RequestBody UserDto userDto) {

        // register password while it's not encoding yet to login
        String decodePwd = userDto.getPassword();
        // create user in the application
        if(!authService.createNewUser(userDto)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        // connect user to the applications
        CredentialDto connect = new CredentialDto();
        connect.setPseudo(userDto.getPseudo());
        connect.setPassword(decodePwd);
        try {
            return new ResponseEntity<>(authService.getLogin(connect), HttpStatus.CREATED);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


}
