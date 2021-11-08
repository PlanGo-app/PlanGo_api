package com.plango.api.controller;

import com.plango.api.dto.CredentialDto;
import com.plango.api.entity.User;
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
    UserService userService;

    @Autowired
    JwtGenerator jwtGenerator;

    @PostMapping("/login")
    ResponseEntity<String> login(@RequestBody CredentialDto credentials) throws Exception {

    	try {
    		
    		UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword());

    		Authentication auth = authenticationManager.authenticate(upat);
            SecurityContextHolder.getContext().setAuthentication(auth);
            String token = jwtGenerator.generateToken(auth);

            return new ResponseEntity<String>(token, HttpStatus.OK);
            
    	}
    	catch (AuthenticationException e) {
    		throw new Exception("Wrong pseudo or password.");
    	}
        
    }


}
