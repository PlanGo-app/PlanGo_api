package com.plango.api.controller;

import com.plango.api.common.constant.ExceptionMessage;
import com.plango.api.common.exception.UserAlreadyExistsException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.controller.impl.UserControllerImpl;
import com.plango.api.dto.UserDto;
import com.plango.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @InjectMocks
    UserControllerImpl userController;

    @Mock
    UserService userService;

    private UserDto currentUserDto;

    private static final String CURRENT_USER_PSEUDO = "currentUser";
    private static final Long CURRENT_USER_ID = 10L;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        currentUserDto = new UserDto();
        currentUserDto.setPseudo(CURRENT_USER_PSEUDO);
    }

    @Test
    public void currentUserNotConnected() throws UserNotFoundException {
        when(userService.getCurrentUser()).thenThrow(new UserNotFoundException(ExceptionMessage.CURRENT_USER_NOT_FOUND));

        ResponseEntity<UserDto> response = userController.getCurrentUser();

        verify(userService).getCurrentUser();
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void currentUserConnected() throws UserNotFoundException {
        when(userService.getCurrentUser()).thenReturn(currentUserDto);

        ResponseEntity<UserDto> response = userController.getCurrentUser();

        verify(userService).getCurrentUser();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(currentUserDto, response.getBody());
    }

    @Test
    public void createUserThatAlreadyExists() throws UserAlreadyExistsException {
        doThrow(new UserAlreadyExistsException("")).when(userService).createUser(currentUserDto);

        ResponseEntity<String> response = userController.createUser(currentUserDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void createUserThatDoesntExist() throws UserAlreadyExistsException {
        ResponseEntity<String> response = userController.createUser(currentUserDto);
        verify(userService).createUser(currentUserDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains(currentUserDto.getPseudo()));
    }
}
