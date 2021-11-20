package com.plango.api.controller.impl;

import com.plango.api.controller.AuthController;
import com.plango.api.controller.UserController;
import com.plango.api.dto.CredentialDto;
import com.plango.api.dto.UserDto;
import com.plango.api.security.JwtGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthControllerImpl implements AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserController userController;

    @Autowired
    JwtGenerator jwtGenerator;

    /**
     * User log in the application
     * @param credentials pseudo and password' user
     * @return : token, or else : exception message
     */
    public ResponseEntity<String> login(@RequestBody CredentialDto credentials) {

    	try {
    		UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword());
    		
    		Authentication auth = authenticationManager.authenticate(upat);
            SecurityContextHolder.getContext().setAuthentication(auth);
            String token = jwtGenerator.generateToken(auth);

            return new ResponseEntity<>(token, HttpStatus.OK);
    	}
    	catch (AuthenticationException e) {
    		return new ResponseEntity<>("Wrong pseudo or password.", HttpStatus.BAD_REQUEST);
    	}
        
    }

    /**
     * New user sign up to the application
     * @param userDto all new user essential information
     * @return : token, or else : exception message
     */
    public ResponseEntity<String> signup(@RequestBody UserDto userDto) {

        // register password while it's not encoding yet to login
        String decodePwd = userDto.getPassword();
        // create user in the application
        ResponseEntity<String> create = userController.createUser(userDto);
        if(create.getStatusCode() == HttpStatus.BAD_REQUEST) {
        	return new ResponseEntity<>(create.getBody(), HttpStatus.BAD_REQUEST);
        }

        // connect user to the applications
        CredentialDto connect = new CredentialDto();
        connect.setUsername(userDto.getPseudo());
        connect.setPassword(decodePwd);
        try {
            return this.login(connect);
        }catch(Exception e){
            return new ResponseEntity<>("Couldn't create JWT token.", HttpStatus.BAD_REQUEST);
        }
    }


}
