package com.plango.api.controller;

import com.plango.api.common.constant.ExceptionMessage;
import com.plango.api.common.exception.UserAlreadyExistsException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.controller.impl.UserControllerImpl;
import com.plango.api.dto.travel.GetTravelDto;
import com.plango.api.dto.user.UserDto;
import com.plango.api.dto.travel.UserTravelsDto;
import com.plango.api.dto.user.UserUpdateDto;
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

import java.util.ArrayList;
import java.util.List;

public class UserControllerTest {

    @InjectMocks
    UserControllerImpl userController;

    @Mock
    UserService userService;

    private UserDto currentUserDto;
    private UserUpdateDto updateDto;
    private GetTravelDto oneTravel;
    private List<GetTravelDto> listTravels;

    private static final String CURRENT_USER_PSEUDO = "currentUser";
    private static final Long TRAVEL_DTO_ID = 10L;
    private static final String UPDATE_USER_EMAIL = "user@test.com";

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        currentUserDto = new UserDto();
        currentUserDto.setPseudo(CURRENT_USER_PSEUDO);
        updateDto = new UserUpdateDto();
        updateDto.setEmail(UPDATE_USER_EMAIL);
        oneTravel = new GetTravelDto();
        oneTravel.setId(TRAVEL_DTO_ID);
        listTravels = new ArrayList();
        listTravels.add(oneTravel);
    }

    @Test
    public void currentUserNotFound() throws UserNotFoundException {
        when(userService.getCurrentUser()).thenThrow(new UserNotFoundException(ExceptionMessage.CURRENT_USER_NOT_FOUND));

        ResponseEntity<UserDto> response = userController.getCurrentUser();

        verify(userService).getCurrentUser();
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void currentUserFound() throws UserNotFoundException {
        when(userService.getCurrentUser()).thenReturn(currentUserDto);

        ResponseEntity<UserDto> response = userController.getCurrentUser();

        verify(userService).getCurrentUser();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(currentUserDto, response.getBody());
    }

    @Test
    public void createUserThatAlreadyExists() throws UserAlreadyExistsException {
        doThrow(new UserAlreadyExistsException(ExceptionMessage.PSEUDO_EMAIL_TAKEN)).when(userService).createUser(currentUserDto);

        ResponseEntity<String> response = userController.createUser(currentUserDto);

        verify(userService).createUser(currentUserDto);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void createUserThatDoesntExist() throws UserAlreadyExistsException {
        ResponseEntity<String> response = userController.createUser(currentUserDto);

        verify(userService).createUser(currentUserDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains(currentUserDto.getPseudo()));
    }

    @Test
    public void updateCurrentUserNotFound() throws UserNotFoundException {
        doThrow(new UserNotFoundException(ExceptionMessage.CURRENT_USER_NOT_FOUND)).when(userService).updateUser(updateDto);

        ResponseEntity<String> response = userController.updateCurrentUser(updateDto);

        verify(userService).updateUser(updateDto);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void updateCurrentUserFound() throws UserNotFoundException {
        ResponseEntity<String> response = userController.updateCurrentUser(updateDto);
        
        verify(userService).updateUser(updateDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void deleteCurrentUserNotFound() throws UserNotFoundException {
        doThrow(new UserNotFoundException(ExceptionMessage.CURRENT_USER_NOT_FOUND)).when(userService).deleteCurrentUser();

        ResponseEntity<String> response = userController.deleteCurrentUser();

        verify(userService).deleteCurrentUser();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void deleteCurrentUserFound() throws UserNotFoundException {
        ResponseEntity<String> response = userController.deleteCurrentUser();
        
        verify(userService).deleteCurrentUser();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getTravelsOfCurrentUserNotFound() throws UserNotFoundException {
        doThrow(new UserNotFoundException(ExceptionMessage.CURRENT_USER_NOT_FOUND)).when(userService).getTravels();

        ResponseEntity<UserTravelsDto> response = userController.getTravelsOfCurrentUser();

        verify(userService).getTravels();
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getTravelsOfCurrentUserFound() throws UserNotFoundException {
        when(userService.getTravels()).thenReturn(listTravels);

        ResponseEntity<UserTravelsDto> response = userController.getTravelsOfCurrentUser();

        verify(userService).getTravels();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getTravels(), listTravels);
    }

}
