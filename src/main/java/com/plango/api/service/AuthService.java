package com.plango.api.service;

import com.plango.api.common.component.AuthenticationFacade;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.controller.UserController;
import com.plango.api.dto.AuthDto;
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
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserController userController;

    @Autowired
    JwtGenerator jwtGenerator;

    @Autowired
    AuthenticationFacade authenticationFacade;

    public AuthDto getLogin(CredentialDto credentials) throws AuthenticationException, UserNotFoundException {

    	UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(credentials.getPseudo(), credentials.getPassword());
    		
    	Authentication auth = authenticationManager.authenticate(upat);
        SecurityContextHolder.getContext().setAuthentication(auth);

        AuthDto loginInformation = new AuthDto();
        loginInformation.setToken(jwtGenerator.generateToken(auth));
        loginInformation.setUserId(authenticationFacade.getCurrentUser().getId());

        return loginInformation;
    }

    public boolean createNewUser(UserDto userDto){
        ResponseEntity<String> create = userController.createUser(userDto);
        if(create.getStatusCode() == HttpStatus.BAD_REQUEST) {
        	return false; 
        }
        return true;
    }
}
