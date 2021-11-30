package com.plango.api.service;

import com.plango.api.common.component.IAuthenticationFacade;
import com.plango.api.common.exception.UserAlreadyExistsException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.dto.authentication.AuthDto;
import com.plango.api.dto.authentication.CredentialDto;
import com.plango.api.dto.user.UserDto;
import com.plango.api.entity.User;
import com.plango.api.security.JwtGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthServiceTest{

    @InjectMocks
    AuthService authService;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    UserService userService;

    @Mock
    JwtGenerator jwtGenerator;

    @Mock
    IAuthenticationFacade authenticationFacade;

    private CredentialDto credentials;
    private UserDto newUser;
    private Authentication auth;
    private UsernamePasswordAuthenticationToken userAuthToken;
    private User currentUser;

    private static final String CREDENTIALS = "test";
    private static final String EMAIL = "test@gmail.com";
    private static final String TOKEN = "tokenTest";
    private static final Long CURRENT_USER_ID = 10L;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        credentials = new CredentialDto();
        credentials.setUsername(CREDENTIALS);
        credentials.setPassword(CREDENTIALS);
        newUser = new UserDto();
        newUser.setEmail(EMAIL);
        newUser.setPseudo(CREDENTIALS);
        newUser.setPassword(CREDENTIALS);
        userAuthToken = new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword());
        auth = new TestingAuthenticationToken(credentials, credentials.getPassword());
        currentUser = new User();
        currentUser.setId(CURRENT_USER_ID);
        currentUser.setPseudo(CREDENTIALS);
    }

    @Test
    public void userCannotBeAuthenticated() {
        when(authenticationManager.authenticate(userAuthToken)).thenThrow(new BadCredentialsException(anyString()));

        assertThatExceptionOfType(BadCredentialsException.class)
                .isThrownBy(() -> authService.getLogin(credentials));
    }

    @Test
    public void shouldReturnValidLoginInformations() throws UserNotFoundException {
        when(authenticationManager.authenticate(userAuthToken)).thenReturn(auth);
        when(jwtGenerator.generateToken(auth)).thenReturn(TOKEN);
        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);

        AuthDto loginInformations = authService.getLogin(credentials);

        verify(authenticationManager).authenticate(userAuthToken);
        verify(jwtGenerator).generateToken(auth);

        assertEquals(TOKEN, loginInformations.getToken());
        assertEquals(CURRENT_USER_ID, loginInformations.getUserId());
    }

    @Test
    public void tryToCreateUserThatAlreadyExists() throws UserAlreadyExistsException {
        doThrow(new UserAlreadyExistsException("")).when(userService).createUser(newUser);
        assertFalse(authService.createNewUser(newUser));
    }
}
