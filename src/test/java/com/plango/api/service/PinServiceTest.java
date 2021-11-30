package com.plango.api.service;

import com.plango.api.common.component.IAuthenticationFacade;
import com.plango.api.common.component.UserRight;
import com.plango.api.common.constant.ExceptionMessage;
import com.plango.api.common.exception.*;
import com.plango.api.dto.pin.CreatePinDto;
import com.plango.api.dto.pin.GetPinDto;
import com.plango.api.dto.planningevent.CreatePlanningEventDto;
import com.plango.api.dto.planningevent.GetPlanningEventDto;
import com.plango.api.dto.planningevent.PlanningEventDto;
import com.plango.api.entity.Pin;
import com.plango.api.entity.PlanningEvent;
import com.plango.api.entity.Travel;
import com.plango.api.entity.User;
import com.plango.api.repository.PinRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.method.P;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class PinServiceTest {
    private static final Long PIN_ID = 1L;
    private static final String PIN_NAME = "PIN";
    private static final Float LATITUDE = 1.234F;
    private static final Float LONGITUDE = 5.678F;
    private static final Long CURRENT_USER_ID = 2L;
    private static final String CURRENT_USER_PSEUDO = "Current user pseudo";
    private static final Long NOT_CURRENT_USER_ID = 1234L;
    private static final String NOT_CURRENT_USER_PSEUDO = "Not current user pseudo";
    private static final Long TRAVEL_ID = 3L;

    @InjectMocks
    PinService pinService = new PinService();

    @Captor
    ArgumentCaptor<Pin> pinCaptor;

    @Captor
    ArgumentCaptor<CreatePlanningEventDto> createPlanningEventDtoCaptor;

    @Mock
    PinRepository pinRepository;

    @Mock
    IAuthenticationFacade authenticationFacade;

    @Mock
    PlanningEventService planningEventService;

    @Mock
    UserRight userRight;

    @Mock
    ModelMapper mapper;

    User currentUser;
    User notCurrentUser;
    Travel travel;
    PlanningEvent planningEvent;

    @BeforeEach
    void setUp() throws UserNotFoundException {
        MockitoAnnotations.openMocks(this);
        currentUser = new User();
        currentUser.setId(CURRENT_USER_ID);
        currentUser.setPseudo(CURRENT_USER_PSEUDO);
        notCurrentUser = new User();
        notCurrentUser.setId(NOT_CURRENT_USER_ID);
        notCurrentUser.setPseudo(NOT_CURRENT_USER_PSEUDO);
        travel = new Travel();
        travel.setId(TRAVEL_ID);
        travel.setCreatedBy(currentUser);
        planningEvent = new PlanningEvent();
        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void shouldReturnRequiredEntity_OnGetPin_whenCurrentUserHasReadRight() throws PinNotFoundException, CurrentUserAuthorizationException {
        //ARRANGE
        Pin pin = buildPin();
        GetPinDto expectedGetPinDto = buildExpectedGetPinDto();
        when(pinRepository.findById(PIN_ID)).thenReturn(Optional.of(pin));
        when(mapper.map(pin, GetPinDto.class)).thenReturn(expectedGetPinDto);
        when(userRight.currentUserCanRead(travel)).thenReturn(true);

        //ACT
        GetPinDto result = pinService.getPinById(PIN_ID);

        //ASSERT
        assertThat(result).isEqualTo(expectedGetPinDto);
    }

    @Test
    void shouldThrowCUAException_OnGetPin_WhenCurrentUserHasNotReadRight() throws CurrentUserAuthorizationException {
        //ARRANGE
        Pin pin = buildPin();
        GetPinDto expectedGetPlanningEventDto = buildExpectedGetPinDto();
        when(pinRepository.findById(PIN_ID)).thenReturn(Optional.of(pin));
        when(mapper.map(pin, GetPinDto.class)).thenReturn(expectedGetPlanningEventDto);
        when(userRight.currentUserCanRead(travel)).thenReturn(false);

        //ACT
        //ASSERT
        assertThatExceptionOfType(CurrentUserAuthorizationException.class)
                .isThrownBy(() -> pinService.getPinById(PIN_ID))
                .withMessage(ExceptionMessage.CURRENT_USER_NOT_ALLOWED_TO_GET_PIN);
    }

    @Test
    void shouldSaveNewEntity_onCreatePin() throws CurrentUserAuthorizationException, UserNotFoundException, TravelNotFoundException, InvalidRequestDataException, PinAlreadyExistException {
        //ARRANGE
        CreatePinDto createPinDto = buildCreatePinDto();
        Pin pinToSave = buildMappedPinFromCreateDto();
        CreatePlanningEventDto planningEventDtoToCreate = buildMappedCreatePlanningEventDtoFromPlanningEvent(pinToSave);
        when(pinRepository.findById(PIN_ID)).thenReturn(Optional.empty());
        when(mapper.map(createPinDto, Pin.class)).thenReturn(pinToSave);
        when(mapper.map(planningEvent, CreatePlanningEventDto.class)).thenReturn(buildMappedCreatePlanningEventDtoFromPlanningEvent(pinToSave));
        when(userRight.currentUserCanWrite(travel)).thenReturn(true);
        //ACT
        pinService.createPin(createPinDto);

        //ASSERT
        verify(pinRepository).save(pinCaptor.capture());
        Pin pinSaved = pinCaptor.getValue();
        assertThat(pinSaved).usingRecursiveComparison().isEqualTo(buildExpectedCreatedPin());

        verify(planningEventService).createPlanningEvent(createPlanningEventDtoCaptor.capture());
        CreatePlanningEventDto createPlanningEventDto = createPlanningEventDtoCaptor.getValue();
        assertThat(createPlanningEventDto).usingRecursiveComparison().isEqualTo(buildMappedCreatePlanningEventDtoFromPlanningEvent(pinToSave));

    }

    /* BUILDERS - GET */

    private Pin buildPin() {
        Pin pin = new Pin();
        pin.setId(PIN_ID);
        pin.setName(PIN_NAME);
        pin.setCreatedBy(currentUser);
        pin.setTravel(travel);
        pin.setLatitude(LATITUDE);
        pin.setLongitude(LONGITUDE);
        pin.setPlanningEvent(planningEvent);
        return pin;
    }

    private GetPinDto buildExpectedGetPinDto() {
        GetPinDto getPinDto = new GetPinDto();
        getPinDto.setId(PIN_ID);
        getPinDto.setName(PIN_NAME);
        getPinDto.setCreatedBy(currentUser);
        getPinDto.setTravel(travel);
        getPinDto.setLatitude(LATITUDE);
        getPinDto.setLongitude(LONGITUDE);
        getPinDto.setPlanningEvent(planningEvent);
        return getPinDto;
    }

    /* BUILDERS - CREATE */

    private CreatePinDto buildCreatePinDto() {
        CreatePinDto createPinDto = new CreatePinDto();
        createPinDto.setName(PIN_NAME);
        createPinDto.setTravel(travel);
        createPinDto.setLatitude(LATITUDE);
        createPinDto.setLongitude(LONGITUDE);
        return createPinDto;
    }

    private Pin buildExpectedCreatedPin() {
        Pin pin = new Pin();
        pin.setName(PIN_NAME);
        pin.setCreatedBy(currentUser);
        pin.setTravel(travel);
        pin.setLatitude(LATITUDE);
        pin.setLongitude(LONGITUDE);
        pin.setPlanningEvent(planningEvent);
        return pin;
    }

    private Pin buildMappedPinFromCreateDto() {
        Pin pin = new Pin();
        pin.setName(PIN_NAME);
        pin.setTravel(travel);
        pin.setLatitude(LATITUDE);
        pin.setLongitude(LONGITUDE);
        return pin;
    }

    private CreatePlanningEventDto buildMappedCreatePlanningEventDtoFromPlanningEvent(Pin pin) {
        CreatePlanningEventDto createPlanningEventDto = new CreatePlanningEventDto();
        createPlanningEventDto.setName(PIN_NAME);
        createPlanningEventDto.setTravel(travel);
        createPlanningEventDto.setPin(pin);
        return createPlanningEventDto;
    }
}
