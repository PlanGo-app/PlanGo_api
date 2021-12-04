package com.plango.api.service;

import com.plango.api.common.component.IAuthenticationFacade;
import com.plango.api.common.component.UserRight;
import com.plango.api.common.constant.ExceptionMessage;
import com.plango.api.common.exception.*;
import com.plango.api.common.types.TransportType;
import com.plango.api.dto.planningevent.CreatePlanningEventDto;
import com.plango.api.dto.planningevent.GetPlanningEventDto;
import com.plango.api.dto.planningevent.UpdatePlanningEventDto;
import com.plango.api.entity.*;
import com.plango.api.repository.PlanningEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
class PlanningEventServiceTest {
    private static final LocalDateTime DATE_START = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
    private static final LocalDateTime DATE_END = LocalDateTime.of(2016, Month.JULY, 29, 19, 30, 40);
    private static final LocalDateTime DATE_END_INVALID = LocalDateTime.of(2014, Month.JULY, 29, 19, 30, 40);
    private static final LocalDateTime DATE_END_UPDATED = LocalDateTime.of(2017, Month.JULY, 29, 19, 30, 40);
    private static final Long EVENT_ID = 1L;
    private static final String EVENT_NAME = "Test Event";
    private static final String EVENT_NAME_UPDATED = "Test Event Updated";
    private static final Long CURRENT_USER_ID = 2L;
    private static final Long NOT_CURRENT_USER_ID = 1234L;
    private static final String CURRENT_USER_PSEUDO = "Current user pseudo";
    private static final String NOT_CURRENT_USER_PSEUDO = "Not current user pseudo";
    private static final Long TRAVEL_ID = 3L;

    @InjectMocks
    PlanningEventService planningEventService = new PlanningEventService();

    @Captor
    ArgumentCaptor<PlanningEvent> planningEventCaptor;

    @Captor
    ArgumentCaptor<Long> idCaptor;

    @Mock
    PlanningEventRepository planningEventRepository;

    @Mock
    IAuthenticationFacade authenticationFacade;

    @Mock
    TravelService travelService;

    @Mock
    ModelMapper modelMapper;

    @Mock
    UserRight userRight;

    User currentUser;
    User notCurrentUser;
    Travel travel;
    Pin pin;

    @BeforeEach
    void setUp() throws CurrentUserAuthorizationException {
        MockitoAnnotations.openMocks(this);
        currentUser = new User();
        currentUser.setId(CURRENT_USER_ID);
        currentUser.setPseudo(CURRENT_USER_PSEUDO);
        notCurrentUser = new User();
        notCurrentUser.setId(NOT_CURRENT_USER_ID);
        notCurrentUser.setPseudo(NOT_CURRENT_USER_PSEUDO);
        travel = new Travel();
        travel.setId(TRAVEL_ID);
        pin = new Pin();
        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void shouldReturnRequiredEntity_OnGetPlanningEvent_whenCurrentUserHasReadRight() throws PlanningEventNotFoundException, CurrentUserAuthorizationException {
        //ARRANGE
        PlanningEvent planningEvent = buildPlanningEvent();
        GetPlanningEventDto expectedGetPlanningEventDto = buildExpectedGetPlanningEventDto();
        when(planningEventRepository.findById(EVENT_ID)).thenReturn(Optional.of(planningEvent));
        when(modelMapper.map(planningEvent, GetPlanningEventDto.class)).thenReturn(expectedGetPlanningEventDto);
        when(userRight.currentUserCanRead(travel)).thenReturn(true);

        //ACT
        GetPlanningEventDto result = planningEventService.getPlanningEventById(EVENT_ID);

        //ASSERT
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedGetPlanningEventDto);
    }

    @Test
    void shouldThrowCUAException_OnGetPlanningEvent_WhenCurrentUserHasNotReadRight() throws CurrentUserAuthorizationException {
        //ARRANGE
        PlanningEvent planningEvent = buildPlanningEvent();
        GetPlanningEventDto expectedGetPlanningEventDto = buildExpectedGetPlanningEventDto();
        when(planningEventRepository.findById(EVENT_ID)).thenReturn(Optional.of(planningEvent));
        when(modelMapper.map(planningEvent, GetPlanningEventDto.class)).thenReturn(expectedGetPlanningEventDto);
        when(userRight.currentUserCanRead(travel)).thenReturn(false);

        //ACT
        //ASSERT
        assertThatExceptionOfType(CurrentUserAuthorizationException.class)
                .isThrownBy(() -> planningEventService.getPlanningEventById(EVENT_ID))
                .withMessage(ExceptionMessage.CURRENT_USER_NOT_ALLOWED_TO_GET_PLANNING_EVENT);
    }

    @Test
    void shouldSaveNewEntity_onCreatePlanningEvent() throws TravelNotFoundException, InvalidRequestDataException, CurrentUserAuthorizationException {
        //ARRANGE
        CreatePlanningEventDto createPlanningEventDto = buildCreatePlanningEventDto();
        when(travelService.getTravelById(anyLong())).thenReturn(travel);
        when(modelMapper.map(createPlanningEventDto, PlanningEvent.class)).thenReturn(buildMappedPlanningEventFromCreateDto());
        when(userRight.currentUserCanWrite(travel)).thenReturn(true);

        //ACT
        planningEventService.createPlanningEvent(createPlanningEventDto);

        //ASSERT
        verify(planningEventRepository).save(planningEventCaptor.capture());
        PlanningEvent planningEventSaved = planningEventCaptor.getValue();
        assertThat(planningEventSaved).usingRecursiveComparison().isEqualTo(buildExpectedCreatedPlanningEvent());
    }

    @Test
    void shouldSaveTheUpdatedEntity_onUpdatePlanningEvent_whenCurrentUserHasWriteRight() throws PlanningEventNotFoundException, CurrentUserAuthorizationException, InvalidRequestDataException {
        //ARRANGE
        UpdatePlanningEventDto updatePlanningEventDto = buildUpdatePlanningEventDto();
        when(planningEventRepository.findById(1L)).thenReturn(Optional.of(buildPlanningEvent()));
        when(userRight.currentUserCanWrite(travel)).thenReturn(true);

        //ACT
        planningEventService.updatePlanningEvent(updatePlanningEventDto);

        //ASSERT
        verify(planningEventRepository).save(planningEventCaptor.capture());
        PlanningEvent planningEventSaved = planningEventCaptor.getValue();
        assertThat(planningEventSaved).usingRecursiveComparison().isEqualTo(buildExpectedUpdatedPlanningEvent());
    }

    @Test
    void shouldThrowCUAException_onUpdatePlanningEvent_whenCurrentUserHasNotWriteRight() throws CurrentUserAuthorizationException {
        //ARRANGE
        //ARRANGE
        UpdatePlanningEventDto updatePlanningEventDto = buildUpdatePlanningEventDto();
        PlanningEvent planningEventOfAnotherUser = buildPlanningEvent();
        planningEventOfAnotherUser.setCreatedBy(new User());
        when(planningEventRepository.findById(1L)).thenReturn(Optional.of(planningEventOfAnotherUser));
        when(userRight.currentUserCanWrite(travel)).thenReturn(false);

        //ACT
        //ASSERT
        assertThatExceptionOfType(CurrentUserAuthorizationException.class)
                .isThrownBy(() -> planningEventService.updatePlanningEvent(updatePlanningEventDto))
                .withMessage(ExceptionMessage.CURRENT_USER_NOT_ALLOWED_TO_UPDATE_PLANNING_EVENT);
    }

    @Test
    void shouldCallDeleteByIdOfThePlanningEventRepo() throws PlanningEventNotFoundException {
        //ARRANGE
        when(planningEventRepository.findById(EVENT_ID)).thenReturn(Optional.of(new PlanningEvent()));

        //ACT
        planningEventService.deletePlanningEventById(EVENT_ID);

        //ASSERT
        verify(planningEventRepository).deleteById(idCaptor.capture());
        Long capturedId = idCaptor.getValue();
        assertThat(capturedId).isEqualTo(EVENT_ID);
    }

    /* BUILDERS - GET */

    private PlanningEvent buildPlanningEvent() {
        PlanningEvent planningEvent = new PlanningEvent();
        planningEvent.setId(EVENT_ID);
        planningEvent.setName(EVENT_NAME);
        planningEvent.setTravel(travel);
        planningEvent.setPin(pin);
        planningEvent.setCreatedBy(currentUser);
        planningEvent.setTransportTypeToNext(TransportType.PUBLIC_TRANSPORT);
        planningEvent.setDateStart(DATE_START);
        planningEvent.setDateEnd(DATE_END);
        return planningEvent;
    }

    private PlanningEvent buildPlanningEventNotCurrentUser() {
        PlanningEvent planningEvent = new PlanningEvent();
        planningEvent.setId(EVENT_ID);
        planningEvent.setName(EVENT_NAME);
        planningEvent.setTravel(travel);
        planningEvent.setPin(pin);
        planningEvent.setCreatedBy(notCurrentUser);
        planningEvent.setTransportTypeToNext(TransportType.PUBLIC_TRANSPORT);
        planningEvent.setDateStart(DATE_START);
        planningEvent.setDateEnd(DATE_END);
        return planningEvent;
    }

    private GetPlanningEventDto buildExpectedGetPlanningEventDto() {
        GetPlanningEventDto expectedGetPlanningEventDto = new GetPlanningEventDto();
        expectedGetPlanningEventDto.setId(EVENT_ID);
        expectedGetPlanningEventDto.setName(EVENT_NAME);
        expectedGetPlanningEventDto.setTravelId(travel.getId());
        expectedGetPlanningEventDto.setPinId(pin.getId());
        expectedGetPlanningEventDto.setCreatedBy(currentUser.getId());
        expectedGetPlanningEventDto.setTransportTypeToNext(TransportType.PUBLIC_TRANSPORT);
        expectedGetPlanningEventDto.setDateStart(DATE_START);
        expectedGetPlanningEventDto.setDateEnd(DATE_END);
        return expectedGetPlanningEventDto;
    }


    /* BUILDERS - CREATE */

    private CreatePlanningEventDto buildCreatePlanningEventDto() {
        CreatePlanningEventDto createPlanningEventDto = new CreatePlanningEventDto();
        createPlanningEventDto.setName(EVENT_NAME);
        createPlanningEventDto.setTravel(travel);
        createPlanningEventDto.setPin(pin);
        createPlanningEventDto.setTransportTypeToNext(TransportType.PUBLIC_TRANSPORT);
        createPlanningEventDto.setDateStart(DATE_START);
        createPlanningEventDto.setDateEnd(DATE_END);
        return createPlanningEventDto;
    }

    private PlanningEvent buildExpectedCreatedPlanningEvent() {
        PlanningEvent expectedCreatedPlanningEvent = new PlanningEvent();
        expectedCreatedPlanningEvent.setCreatedBy(currentUser);
        expectedCreatedPlanningEvent.setName(EVENT_NAME);
        expectedCreatedPlanningEvent.setTravel(travel);
        expectedCreatedPlanningEvent.setPin(pin);
        expectedCreatedPlanningEvent.setTransportTypeToNext(TransportType.PUBLIC_TRANSPORT);
        expectedCreatedPlanningEvent.setDateStart(DATE_START);
        expectedCreatedPlanningEvent.setDateEnd(DATE_END);
        return expectedCreatedPlanningEvent;
    }

    private PlanningEvent buildMappedPlanningEventFromCreateDto() {
        PlanningEvent mappedPlanningEventFromCreateDto = new PlanningEvent();
        mappedPlanningEventFromCreateDto.setName(EVENT_NAME);
        mappedPlanningEventFromCreateDto.setTravel(travel);
        mappedPlanningEventFromCreateDto.setPin(pin);
        mappedPlanningEventFromCreateDto.setTransportTypeToNext(TransportType.PUBLIC_TRANSPORT);
        mappedPlanningEventFromCreateDto.setDateStart(DATE_START);
        mappedPlanningEventFromCreateDto.setDateEnd(DATE_END);
        return mappedPlanningEventFromCreateDto;
    }


    /* BUILDERS - UPDATE */

    private UpdatePlanningEventDto buildUpdatePlanningEventDto() {
        UpdatePlanningEventDto updatePlanningEventDto = new UpdatePlanningEventDto();
        updatePlanningEventDto.setId(EVENT_ID);
        updatePlanningEventDto.setName(EVENT_NAME_UPDATED);
        updatePlanningEventDto.setDateStart(DATE_START);
        updatePlanningEventDto.setDateEnd(DATE_END_UPDATED);
        return updatePlanningEventDto;
    }

    private PlanningEvent buildExpectedUpdatedPlanningEvent() {
        PlanningEvent planningEvent = new PlanningEvent();
        planningEvent.setId(EVENT_ID);
        planningEvent.setTravel(travel);
        planningEvent.setPin(pin);
        planningEvent.setCreatedBy(currentUser);
        planningEvent.setName(EVENT_NAME_UPDATED);
        planningEvent.setTransportTypeToNext(null);
        planningEvent.setDateStart(DATE_START);
        planningEvent.setDateEnd(DATE_END_UPDATED);
        return planningEvent;
    }

    private PlanningEvent buildExpectedUpdatedPlanningEventNotCurrentUser() {
        PlanningEvent planningEvent = new PlanningEvent();
        planningEvent.setId(EVENT_ID);
        planningEvent.setTravel(travel);
        planningEvent.setPin(pin);
        planningEvent.setCreatedBy(notCurrentUser);
        planningEvent.setName(EVENT_NAME_UPDATED);
        planningEvent.setTransportTypeToNext(null);
        planningEvent.setDateStart(DATE_START);
        planningEvent.setDateEnd(DATE_END_UPDATED);
        return planningEvent;
    }

}
