package com.plango.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.plango.api.controller.AuthController;
import com.plango.api.controller.UserController;
import com.plango.api.controller.impl.AuthControllerImpl;
import com.plango.api.dto.CredentialDto;
import com.plango.api.security.JwtGenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

public class AuthControllerTest {

    @InjectMocks
    private AuthControllerImpl authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserController userController;

    @Mock
    private JwtGenerator jwtGenerator;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginWhenUserDontExists() {
        CredentialDto credentials = new CredentialDto();
        credentials.setUsername("test");
        credentials.setPassword("test");
        ResponseEntity<String> response = authController.login(credentials);
        assertEquals("Wrong pseudo or password.", response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}