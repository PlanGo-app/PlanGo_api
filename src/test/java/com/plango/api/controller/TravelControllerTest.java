package com.plango.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.plango.api.common.constant.ExceptionMessage;
import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.common.exception.TravelNotFoundException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.common.types.Role;
import com.plango.api.controller.impl.TravelControllerImpl;
import com.plango.api.dto.TravelPlanningEventDto;
import com.plango.api.dto.member.MemberDto;
import com.plango.api.dto.member.TravelMembersDto;
import com.plango.api.dto.planningevent.GetPlanningEventDto;
import com.plango.api.dto.travel.CreateTravelDto;
import com.plango.api.dto.travel.GetTravelDto;
import com.plango.api.dto.user.UserDto;
import com.plango.api.entity.Travel;
import com.plango.api.entity.User;
import com.plango.api.service.TravelService;
import com.plango.api.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class TravelControllerTest {

    @InjectMocks
    TravelControllerImpl travelController;

    @Mock
    TravelService travelService;

    @Mock
    UserService userService;

    private static final Long TRAVEL_ID = 100L;
    private static final String TRAVEL_CITY = "testCity";
    private static final String TRAVEL_COUNTRY = "testCountry";
    private static final Long USER_ID = 900L;
    private static final String USER_PSEUDO_PASSWORD = "testCredentials";
    private static final String USER_EMAIL = "testEmail@test.com";
    private static final String OBSERVER_STRING = "OBSERVER";
    private static final Role OBSERVER_ROLE = Role.OBSERVER;
    private static final String NON_EXISTING_ROLE = "TEST";
    private static final String TRAVEL_INVITATION_CODE = "test";
    private static final Long PLANNING_EVENT_ID = 15L;

    private CreateTravelDto createTravelDto;
    private GetTravelDto getTravelDto;
    private Travel oneTravel;
    private Travel newTravel;
    private User oneUser;
    private UserDto userDto;
    private TravelMembersDto travelMembersDto;
    private TravelPlanningEventDto travelPlanningEventDto;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        createTravelDto = buildCreateTravelDto();
        getTravelDto = buildGetTravelDto();
        newTravel = buildNewTravel();
        oneTravel = newTravel;
        oneTravel.setId(TRAVEL_ID);
        oneUser = buildUser();
        travelMembersDto = buildTravelMembersDto();
        userDto = buildUserDto();
        travelPlanningEventDto = buildTravelPlanningEventDto();
    }

    @Test
    void shouldntCreateTravel_CurrentUserNotAuthorized_AndReturnStatusForbidden() throws CurrentUserAuthorizationException {
        when(travelService.convertCreateDtoToEntity(createTravelDto)).thenReturn(newTravel);
        doThrow(new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_CANNOT_BE_AUTHENTICATED))
                .when(travelService).createTravel(newTravel);

        ResponseEntity<String> response = travelController.createTravel(createTravelDto);

        verify(travelService).convertCreateDtoToEntity(createTravelDto);
        verify(travelService).createTravel(newTravel);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(ExceptionMessage.CURRENT_USER_CANNOT_BE_AUTHENTICATED, response.getBody());
    }

    @Test
    void shouldntCreateTravel_MissingOrWrongInformations_AndReturnStatusBadRequest() throws CurrentUserAuthorizationException {
        when(travelService.convertCreateDtoToEntity(createTravelDto)).thenThrow(new IllegalArgumentException());

        ResponseEntity<String> response = travelController.createTravel(createTravelDto);

        verify(travelService).convertCreateDtoToEntity(createTravelDto);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ExceptionMessage.MISSING_OR_WRONG_ARGUMENTS_FOR_TRAVEL_CREATION, response.getBody());
    }

    @Test
    void shouldCreateTravel_AndReturnStatusCreated() throws UserNotFoundException, CurrentUserAuthorizationException {
        when(travelService.convertCreateDtoToEntity(createTravelDto)).thenReturn(newTravel);

        ResponseEntity<String> response = travelController.createTravel(createTravelDto);

        verify(travelService).convertCreateDtoToEntity(createTravelDto);
        verify(travelService).createTravel(newTravel);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("created"));
    }

    @Test
    void shouldntAddMember_CurrentUserNotAuthorized_AndReturnStatusForbidden() throws TravelNotFoundException, UserNotFoundException, CurrentUserAuthorizationException {
        when(travelService.getTravelById(TRAVEL_ID)).thenReturn(oneTravel);
        when(userService.getUserById(USER_ID)).thenReturn(oneUser);
        doThrow(new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_CANNOT_BE_AUTHENTICATED)).when(travelService).addMember(oneTravel, oneUser, OBSERVER_ROLE);

        ResponseEntity<String> response = travelController.addMemberToTravel(TRAVEL_ID, USER_ID, OBSERVER_STRING);
        
        verify(travelService).getTravelById(TRAVEL_ID);
        verify(userService).getUserById(USER_ID);
        verify(travelService).addMember(oneTravel, oneUser, OBSERVER_ROLE);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().contains("not authorized"));
    }

    @Test
    void shouldntAddMember_TravelDoesntExist_AndReturnStatusNotFound() throws TravelNotFoundException, UserNotFoundException, CurrentUserAuthorizationException {
        when(travelService.getTravelById(TRAVEL_ID)).thenThrow(new TravelNotFoundException(ExceptionMessage.TRAVEL_NOT_FOUND));
        
        ResponseEntity<String> response = travelController.addMemberToTravel(TRAVEL_ID, USER_ID, OBSERVER_STRING);
        
        verify(travelService).getTravelById(TRAVEL_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ExceptionMessage.TRAVEL_NOT_FOUND, response.getBody());
    }

    @Test
    void shouldntAddMember_userDoesntExist_AndReturnStatusNotFound() throws TravelNotFoundException, UserNotFoundException, CurrentUserAuthorizationException {
        when(travelService.getTravelById(TRAVEL_ID)).thenReturn(oneTravel);
        when(userService.getUserById(USER_ID)).thenThrow(new UserNotFoundException(ExceptionMessage.USER_NOT_FOUND));

        ResponseEntity<String> response = travelController.addMemberToTravel(TRAVEL_ID, USER_ID, OBSERVER_STRING);
        
        verify(travelService).getTravelById(TRAVEL_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ExceptionMessage.USER_NOT_FOUND, response.getBody());
    }

    @Test
    void shouldntAddMember_WithBadRole_AndReturnStatusNotFound() throws TravelNotFoundException, UserNotFoundException, CurrentUserAuthorizationException {
        when(travelService.getTravelById(TRAVEL_ID)).thenReturn(oneTravel);
        when(userService.getUserById(USER_ID)).thenReturn(oneUser);
        
        ResponseEntity<String> response = travelController.addMemberToTravel(TRAVEL_ID, USER_ID, NON_EXISTING_ROLE);
        
        verify(travelService).getTravelById(TRAVEL_ID);
        verify(userService).getUserById(USER_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains(NON_EXISTING_ROLE));
    }

    @Test
    void shouldAddMember_AndReturnStatusCreated() throws TravelNotFoundException, UserNotFoundException, CurrentUserAuthorizationException {
        when(travelService.getTravelById(TRAVEL_ID)).thenReturn(oneTravel);
        when(userService.getUserById(USER_ID)).thenReturn(oneUser);
        
        ResponseEntity<String> response = travelController.addMemberToTravel(TRAVEL_ID, USER_ID, OBSERVER_STRING);
        
        verify(travelService).getTravelById(TRAVEL_ID);
        verify(userService).getUserById(USER_ID);
        verify(travelService).addMember(oneTravel, oneUser, OBSERVER_ROLE);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("added to travel"));
    }

    @Test
    void shouldntUpdateMember_CurrentUserNotAuthorized_AndReturnStatusForbidden() throws TravelNotFoundException, UserNotFoundException, CurrentUserAuthorizationException {
        when(travelService.getTravelById(TRAVEL_ID)).thenReturn(oneTravel);
        when(userService.getUserById(USER_ID)).thenReturn(oneUser);
        doThrow(new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_CANNOT_BE_AUTHENTICATED)).when(travelService).updateMember(oneTravel, oneUser, OBSERVER_ROLE);

        ResponseEntity<String> response = travelController.updateMemberOfTravel(TRAVEL_ID, USER_ID, OBSERVER_STRING);
        
        verify(travelService).getTravelById(TRAVEL_ID);
        verify(userService).getUserById(USER_ID);
        verify(travelService).updateMember(oneTravel, oneUser, OBSERVER_ROLE);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().contains("not authorized"));
    }

    @Test
    void shouldntUpdateMember_TravelDoesntExist_AndReturnStatusNotFound() throws TravelNotFoundException, UserNotFoundException, CurrentUserAuthorizationException {
        when(travelService.getTravelById(TRAVEL_ID)).thenThrow(new TravelNotFoundException(ExceptionMessage.TRAVEL_NOT_FOUND));
        
        ResponseEntity<String> response = travelController.updateMemberOfTravel(TRAVEL_ID, USER_ID, OBSERVER_STRING);
        
        verify(travelService).getTravelById(TRAVEL_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ExceptionMessage.TRAVEL_NOT_FOUND, response.getBody());
    }

    @Test
    void shouldntUpdateMember_userDoesntExist_AndReturnStatusNotFound() throws TravelNotFoundException, UserNotFoundException, CurrentUserAuthorizationException {
        when(travelService.getTravelById(TRAVEL_ID)).thenReturn(oneTravel);
        when(userService.getUserById(USER_ID)).thenThrow(new UserNotFoundException(ExceptionMessage.USER_NOT_FOUND));

        ResponseEntity<String> response = travelController.updateMemberOfTravel(TRAVEL_ID, USER_ID, OBSERVER_STRING);
        
        verify(travelService).getTravelById(TRAVEL_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ExceptionMessage.USER_NOT_FOUND, response.getBody());
    }

    @Test
    void shouldntUpdateMember_WithBadRole_AndReturnStatusNotFound() throws TravelNotFoundException, UserNotFoundException, CurrentUserAuthorizationException {
        when(travelService.getTravelById(TRAVEL_ID)).thenReturn(oneTravel);
        when(userService.getUserById(USER_ID)).thenReturn(oneUser);
        
        ResponseEntity<String> response = travelController.updateMemberOfTravel(TRAVEL_ID, USER_ID, NON_EXISTING_ROLE);
        
        verify(travelService).getTravelById(TRAVEL_ID);
        verify(userService).getUserById(USER_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains(NON_EXISTING_ROLE));
    }

    @Test
    void shouldUpdateMember_AndReturnStatusOK() throws TravelNotFoundException, UserNotFoundException, CurrentUserAuthorizationException {
        when(travelService.getTravelById(TRAVEL_ID)).thenReturn(oneTravel);
        when(userService.getUserById(USER_ID)).thenReturn(oneUser);
        
        ResponseEntity<String> response = travelController.updateMemberOfTravel(TRAVEL_ID, USER_ID, OBSERVER_STRING);
        
        verify(travelService).getTravelById(TRAVEL_ID);
        verify(userService).getUserById(USER_ID);
        verify(travelService).updateMember(oneTravel, oneUser, OBSERVER_ROLE);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(OBSERVER_STRING));
    }

    @Test
    void shouldntDeleteMember_CurrentUserNotAuthorized_AndReturnStatusForbidden() throws TravelNotFoundException, UserNotFoundException, CurrentUserAuthorizationException {
        when(travelService.getTravelById(TRAVEL_ID)).thenReturn(oneTravel);
        when(userService.getUserById(USER_ID)).thenReturn(oneUser);
        doThrow(new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_CANNOT_BE_AUTHENTICATED)).when(travelService).deleteMember(oneTravel, oneUser);

        ResponseEntity<String> response = travelController.deleteMemberOfTravel(TRAVEL_ID, USER_ID);
        
        verify(travelService).getTravelById(TRAVEL_ID);
        verify(userService).getUserById(USER_ID);
        verify(travelService).deleteMember(oneTravel, oneUser);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().contains("not authorized"));
    }

    @Test
    void shouldntDeleteMember_TravelDoesntExist_AndReturnStatusNotFound() throws TravelNotFoundException, UserNotFoundException, CurrentUserAuthorizationException {
        when(travelService.getTravelById(TRAVEL_ID)).thenThrow(new TravelNotFoundException(ExceptionMessage.TRAVEL_NOT_FOUND));
        
        ResponseEntity<String> response = travelController.deleteMemberOfTravel(TRAVEL_ID, USER_ID);

        verify(travelService).getTravelById(TRAVEL_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ExceptionMessage.TRAVEL_NOT_FOUND, response.getBody());
    }

    @Test
    void shouldntDeleteMember_userDoesntExist_AndReturnStatusNotFound() throws TravelNotFoundException, UserNotFoundException, CurrentUserAuthorizationException {
        when(travelService.getTravelById(TRAVEL_ID)).thenReturn(oneTravel);
        when(userService.getUserById(USER_ID)).thenThrow(new UserNotFoundException(ExceptionMessage.USER_NOT_FOUND));

        ResponseEntity<String> response = travelController.deleteMemberOfTravel(TRAVEL_ID, USER_ID);

        verify(travelService).getTravelById(TRAVEL_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ExceptionMessage.USER_NOT_FOUND, response.getBody());
    }

    @Test
    void shouldDeleteMember_AndReturnStatusOK() throws TravelNotFoundException, UserNotFoundException, CurrentUserAuthorizationException {
        when(travelService.getTravelById(TRAVEL_ID)).thenReturn(oneTravel);
        when(userService.getUserById(USER_ID)).thenReturn(oneUser);
        
        ResponseEntity<String> response = travelController.deleteMemberOfTravel(TRAVEL_ID, USER_ID);

        verify(travelService).getTravelById(TRAVEL_ID);
        verify(userService).getUserById(USER_ID);
        verify(travelService).deleteMember(oneTravel, oneUser);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("deleted"));
    }

    @Test
    void shouldntGetMembersOfATravel_TravelDoesntExist_AndReturnStatusNotFound() throws TravelNotFoundException {
        when(travelService.getMembers(TRAVEL_ID)).thenThrow(new TravelNotFoundException(ExceptionMessage.TRAVEL_NOT_FOUND));

        ResponseEntity<TravelMembersDto> response = travelController.getTravelMembers(TRAVEL_ID);

        verify(travelService).getMembers(TRAVEL_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    @Test
    void shouldGetMembersOfATravel_AndReturnStatusOK() throws TravelNotFoundException {
        when(travelService.getMembers(TRAVEL_ID)).thenReturn(travelMembersDto);

        ResponseEntity<TravelMembersDto> response = travelController.getTravelMembers(TRAVEL_ID);

        verify(travelService).getMembers(TRAVEL_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(travelMembersDto, response.getBody());
    } 

    @Test
    void shouldntAddMember_InvitationCodeFalse_AndReturnStatusNotFound() throws TravelNotFoundException, CurrentUserAuthorizationException {
        when(travelService.getTravelByInvitationCode(TRAVEL_INVITATION_CODE)).thenThrow(new TravelNotFoundException(ExceptionMessage.TRAVEL_NOT_FOUND));

        ResponseEntity<GetTravelDto> response = travelController.addMemberToTravelWithInvitation(TRAVEL_INVITATION_CODE);
        
        verify(travelService).getTravelByInvitationCode(TRAVEL_INVITATION_CODE);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldntAddMember_CurrentUserNotConnected_AndReturnStatusNotFound() throws TravelNotFoundException, CurrentUserAuthorizationException {
        when(travelService.getTravelByInvitationCode(TRAVEL_INVITATION_CODE)).thenReturn(getTravelDto);
        when(travelService.getTravelById(TRAVEL_ID)).thenReturn(oneTravel);
        when(userService.getCurrentUser()).thenThrow(new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_CANNOT_BE_AUTHENTICATED));

        ResponseEntity<GetTravelDto> response = travelController.addMemberToTravelWithInvitation(TRAVEL_INVITATION_CODE);
        
        verify(travelService).getTravelByInvitationCode(TRAVEL_INVITATION_CODE);
        verify(travelService).getTravelById(TRAVEL_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldAddMember_WithInvitationCodeValid_AndReturnStatusOK() throws TravelNotFoundException, CurrentUserAuthorizationException {
        when(travelService.getTravelByInvitationCode(TRAVEL_INVITATION_CODE)).thenReturn(getTravelDto);
        when(travelService.getTravelById(TRAVEL_ID)).thenReturn(oneTravel);
        when(userService.getCurrentUser()).thenReturn(oneUser);

        ResponseEntity<GetTravelDto> response = travelController.addMemberToTravelWithInvitation(TRAVEL_INVITATION_CODE);
        
        verify(travelService).getTravelByInvitationCode(TRAVEL_INVITATION_CODE);
        verify(travelService).getTravelById(TRAVEL_ID);
        verify(travelService).addMember(oneTravel, oneUser, OBSERVER_ROLE);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(getTravelDto, response.getBody());
    }

    @Test
    void shouldntGetTravelsPlanningEvent_TravelDoesntExist_AndReturnStatusNotFound() throws TravelNotFoundException {
        when(travelService.getTravelPlanningEvents(TRAVEL_ID)).thenThrow(new TravelNotFoundException(ExceptionMessage.TRAVEL_NOT_FOUND));

        ResponseEntity<TravelPlanningEventDto> response = travelController.getTravelPlanningEvents(TRAVEL_ID);

        verify(travelService).getTravelPlanningEvents(TRAVEL_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldGetTravelsPlanningEventDto_TravelExists_AndReturnStatusOK() throws TravelNotFoundException {
        when(travelService.getTravelPlanningEvents(TRAVEL_ID)).thenReturn(travelPlanningEventDto);

        ResponseEntity<TravelPlanningEventDto> response = travelController.getTravelPlanningEvents(TRAVEL_ID);

        verify(travelService).getTravelPlanningEvents(TRAVEL_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(travelPlanningEventDto, response.getBody());
    }

    private CreateTravelDto buildCreateTravelDto() {
        CreateTravelDto createTravelDto = new CreateTravelDto();
        createTravelDto.setCity(TRAVEL_CITY);
        createTravelDto.setCountry(TRAVEL_COUNTRY);
        return createTravelDto;
    }

    private GetTravelDto buildGetTravelDto() {
        GetTravelDto getTravelDto = new GetTravelDto();
        getTravelDto.setId(TRAVEL_ID);
        getTravelDto.setCity(TRAVEL_CITY);
        getTravelDto.setCountry(TRAVEL_COUNTRY);
        getTravelDto.setInvitationCode(TRAVEL_INVITATION_CODE);
        return getTravelDto;
    }

    private Travel buildNewTravel() {
        Travel newTravel = new Travel();
        newTravel.setCity(TRAVEL_CITY);
        newTravel.setCountry(TRAVEL_COUNTRY);
        return newTravel;
    }

    private User buildUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setPseudo(USER_PSEUDO_PASSWORD);
        user.setPassword(USER_PSEUDO_PASSWORD);
        user.setEmail(USER_EMAIL);
        return user;
    }

    private UserDto buildUserDto() {
        UserDto user = new UserDto();
        user.setPseudo(USER_PSEUDO_PASSWORD);
        user.setPassword(USER_PSEUDO_PASSWORD);
        user.setEmail(USER_EMAIL);
        return user;
    }

    private TravelMembersDto buildTravelMembersDto() {
        MemberDto memberDto = new MemberDto();
        memberDto.setUser(userDto);
        memberDto.setRole(OBSERVER_ROLE);
        List<MemberDto> listMemberDto = new ArrayList<>();
        listMemberDto.add(memberDto);
        return new TravelMembersDto(listMemberDto);
    }

    private TravelPlanningEventDto buildTravelPlanningEventDto() {
        GetPlanningEventDto planningEventDto = new GetPlanningEventDto();
        planningEventDto.setId(PLANNING_EVENT_ID);
        planningEventDto.setTravel(oneTravel);
        List<GetPlanningEventDto> listPlanningEventDto = new ArrayList<>();
        listPlanningEventDto.add(planningEventDto);
        return new TravelPlanningEventDto(listPlanningEventDto);
    }
}
