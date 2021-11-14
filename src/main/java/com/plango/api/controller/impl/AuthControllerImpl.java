package com.plango.api.controller.impl;

import com.plango.api.controller.UserController;
import com.plango.api.dto.CredentialDto;
import com.plango.api.entity.User;
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
@RequestMapping("/auth")
public class AuthControllerImpl {

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
    ResponseEntity<String> login(@RequestBody CredentialDto credentials) {

    	try {
    		UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword());
    		System.out.println(upat.getCredentials());
    		System.out.println(upat.getPrincipal());
    		
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
     * @param newUser all new user essential information
     * @return : token, or else : exception message
     */
    ResponseEntity<String> signup(@RequestBody User newUser) {

        // register password while it's not encoding yet to login
        String decodePwd = newUser.getPassword();
        // create user in the application
        ResponseEntity<String> create = userController.createUser(newUser);
        if(create.getStatusCode() == HttpStatus.BAD_REQUEST) {
        	return new ResponseEntity<>(create.getBody(), HttpStatus.BAD_REQUEST);
        }

        // connect user to the applications
        CredentialDto connect = new CredentialDto();
        connect.setUsername(newUser.getPseudo());
        connect.setPassword(decodePwd);
        try {
            return this.login(connect);
        }catch(Exception e){
            return new ResponseEntity<>("Couldn't create JWT token.", HttpStatus.BAD_REQUEST);
        }
    }


}
