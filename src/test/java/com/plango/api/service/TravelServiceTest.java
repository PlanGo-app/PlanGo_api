package com.plango.api.service;

import com.plango.api.common.component.CodeGenerator;
import com.plango.api.common.component.IAuthenticationFacade;
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
import com.plango.api.repository.TravelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class TravelServiceTest {

    @InjectMocks
    TravelService travelService;

    @Mock
    TravelRepository travelRepository;

    @Mock
    MemberService memberService;

    @Mock
    PlanningEventService planningEventService;

    @Mock
    IAuthenticationFacade authenticationFacade;

    @Mock
    CodeGenerator codeGenerator;

    @Mock
    ModelMapper modelMapper;

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
        getTravelDto = buildGetTravelDto();
        oneTravel = buildTravel();
        oneUser = buildUser();
        travelMembersDto = buildTravelMembersDto();
        userDto = buildUserDto();
        travelPlanningEventDto = buildTravelPlanningEventDto();
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

    private Travel buildTravel() {
        Travel newTravel = new Travel();
        newTravel.setId(TRAVEL_ID);
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
