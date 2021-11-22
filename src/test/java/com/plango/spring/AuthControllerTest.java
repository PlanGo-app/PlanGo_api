package com.plango.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.plango.api.controller.UserController;
import com.plango.api.controller.impl.AuthControllerImpl;
import com.plango.api.dto.CredentialDto;
import com.plango.api.dto.UserDto;
import com.plango.api.security.JwtGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public class AuthControllerTest {

    @InjectMocks
    private AuthControllerImpl authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserController userController;

    @Mock
    private JwtGenerator jwtGenerator;

    private CredentialDto credentials;
    private UserDto newUser;

    final String TestCredentials = "test";
    final String TestEmail = "test@gmail.com";

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        credentials = new CredentialDto();
        credentials.setUsername(TestCredentials);
        credentials.setPassword(TestCredentials);
        newUser = new UserDto();
        newUser.setEmail(TestEmail);
        newUser.setPseudo(TestCredentials);
        newUser.setPassword(TestCredentials);
    }

    @Test
    public void testLoginWhenBadCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException(anyString()));

        ResponseEntity<String> response = authController.login(credentials);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testLoginWithValidUser() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(new TestingAuthenticationToken(credentials, credentials.getPassword()));
        when(jwtGenerator.generateToken(any(Authentication.class))).thenReturn("token");

        ResponseEntity<String> response = authController.login(credentials);

        verify(jwtGenerator).generateToken(any(Authentication.class));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void userAlreadyExistsWhenSignUp() {
        when(userController.createUser(newUser)).thenReturn(new ResponseEntity<String>(anyString(), HttpStatus.BAD_REQUEST));

        ResponseEntity<String> response = authController.signup(newUser);

        verify(userController).createUser(newUser);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}