package com.plango.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.controller.impl.AuthControllerImpl;
import com.plango.api.dto.authentication.AuthDto;
import com.plango.api.dto.authentication.CredentialDto;
import com.plango.api.dto.user.UserDto;

import com.plango.api.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

class AuthControllerTest {

    @InjectMocks
    private AuthControllerImpl authController;

    @Mock
    private AuthService authService;

    private CredentialDto credentials;
    private UserDto newUser;
    private AuthDto newAuth;

    private static final String CREDENTIALS = "test";
    private static final String EMAIL = "test@gmail.com";
    private static final String TOKEN = "tokenTest";
    private static final Long CURRENT_USER_ID = 10L;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        credentials = new CredentialDto();
        credentials.setPseudo(CREDENTIALS);
        credentials.setPassword(CREDENTIALS);
        newUser = new UserDto();
        newUser.setEmail(EMAIL);
        newUser.setPseudo(CREDENTIALS);
        newUser.setPassword(CREDENTIALS);
        newAuth = new AuthDto();
        newAuth.setToken(TOKEN);
        newAuth.setUserId(CURRENT_USER_ID);
    }

    @Test
    void loginWhenBadCredentials() throws CurrentUserAuthorizationException {
        when(authService.getLogin(credentials)).thenThrow(new BadCredentialsException(anyString()));

        ResponseEntity<AuthDto> response = authController.login(credentials);

        verify(authService).getLogin(credentials);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void loginWithValidUser() throws CurrentUserAuthorizationException {
        when(authService.getLogin(credentials)).thenReturn(newAuth);

        ResponseEntity<AuthDto> response = authController.login(credentials);

        verify(authService).getLogin(credentials);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newAuth, response.getBody());
    }

    @Test
    void signUpWithUserThatAlreadyExists() {
        when(authService.createNewUser(newUser)).thenReturn(false);

        ResponseEntity<AuthDto> response = authController.signup(newUser);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void signUpButCannotLogin() throws CurrentUserAuthorizationException {
        when(authService.createNewUser(newUser)).thenReturn(true);
        when(authService.getLogin(any(CredentialDto.class))).thenThrow(new CurrentUserAuthorizationException(""));

        ResponseEntity<AuthDto> response = authController.signup(newUser);

        verify(authService).createNewUser(newUser);
        verify(authService).getLogin(any(CredentialDto.class));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void signUpAndCanLogin() throws CurrentUserAuthorizationException {
        when(authService.createNewUser(newUser)).thenReturn(true);
        when(authService.getLogin(any(CredentialDto.class))).thenReturn(newAuth);

        ResponseEntity<AuthDto> response = authController.signup(newUser);

        verify(authService).createNewUser(newUser);
        verify(authService).getLogin(any(CredentialDto.class));
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newAuth, response.getBody());
    }
}