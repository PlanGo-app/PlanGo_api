package com.plango.api.controller;

import com.plango.api.dto.CredentialDto;
import com.plango.api.entity.User;
import com.plango.api.repository.UserRepository;
import com.plango.api.security.JwtGenerator;
import com.plango.api.service.UserService;

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
public class AuthController {

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
     * @throws Exception if authentification failure
     */
    @PostMapping("/login")
    ResponseEntity<String> login(@RequestBody CredentialDto credentials) throws Exception {

    	try {
    		UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword());
    		System.out.println(upat.getCredentials());
    		System.out.println(upat.getPrincipal());
    		
    		Authentication auth = authenticationManager.authenticate(upat);
            SecurityContextHolder.getContext().setAuthentication(auth);
            String token = jwtGenerator.generateToken(auth);

            return new ResponseEntity<String>(token, HttpStatus.OK);
    	}
    	catch (AuthenticationException e) {
    		throw new Exception("Wrong pseudo or password.");
    	}
        
    }

    /**
     * New user sign up to the application
     * @param newUser all new user essential information
     * @return : token, or else : exception message
     * @throws Exception if user already exists or when problem with the authentification
     */
    @PostMapping("/signup")
    ResponseEntity<String> signup(@RequestBody User newUser) throws Exception {

        // register password while it's not encoding yet to login
        String decodePwd = newUser.getPassword();
        // create user in the application
        ResponseEntity<String> create = userController.createUser(newUser);
        if(create.getStatusCode() == HttpStatus.BAD_REQUEST) {
        	throw new Exception(create.getBody());
        }

        // connect user to the applications
        CredentialDto connect = new CredentialDto();
        connect.setUsername(newUser.getPseudo());
        connect.setPassword(decodePwd);
        try {
            return this.login(connect);
        }catch(Exception e){
            throw new Exception("Couldn't create JWT token.");
        }
    }


}
