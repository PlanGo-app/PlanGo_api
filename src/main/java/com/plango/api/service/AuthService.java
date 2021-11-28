package com.plango.api.service;

import com.plango.api.common.component.IAuthenticationFacade;
import com.plango.api.common.exception.UserAlreadyExistsException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.controller.UserController;
import com.plango.api.dto.authentication.AuthDto;
import com.plango.api.dto.authentication.CredentialDto;
import com.plango.api.dto.user.UserDto;
import com.plango.api.security.JwtGenerator;

import org.springframework.beans.factory.annotation.Autowired;
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
    UserService userService;

    @Autowired
    JwtGenerator jwtGenerator;

    @Autowired
    IAuthenticationFacade authenticationFacade;

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
        try {
            userService.createUser(userDto);
        }
        catch(UserAlreadyExistsException e){
            return false;
        }
        return true;
    }
}
