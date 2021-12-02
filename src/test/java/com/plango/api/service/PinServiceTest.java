package com.plango.api.service;

import com.plango.api.common.component.IAuthenticationFacade;
import com.plango.api.common.component.UserRight;
import com.plango.api.common.constant.ExceptionMessage;
import com.plango.api.common.exception.*;
import com.plango.api.dto.pin.CreatePinDto;
import com.plango.api.dto.pin.GetPinDto;
import com.plango.api.dto.pin.UpdatePinDto;
import com.plango.api.dto.planningevent.CreatePlanningEventDto;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class PinServiceTest {
    private static final Long PIN_ID = 1L;
    private static final String PIN_NAME = "PIN";
    private static final String UPDATED_PIN_NAME = "UPDATED PIN";
    private static final Float LATITUDE = 1.234F;
    private static final Float LONGITUDE = 5.678F;
    private static final Long CURRENT_USER_ID = 2L;
    private static final String CURRENT_USER_PSEUDO = "Current user pseudo";
    private static final Long NOT_CURRENT_USER_ID = 1234L;
    private static final String NOT_CURRENT_USER_PSEUDO = "Not current user pseudo";
    private static final Long TRAVEL_ID = 3L;
    private static final Long PLANNING_EVENT_ID = 4L;

    @InjectMocks
    PinService pinService = new PinService();

    @Captor
    ArgumentCaptor<Pin> pinCaptor;

    @Captor
    ArgumentCaptor<Long> idCaptor;

    @Captor
    ArgumentCaptor<CreatePlanningEventDto> createPlanningEventDtoCaptor;

    @Mock
    PinRepository pinRepository;

    @Mock
    IAuthenticationFacade authenticationFacade;

    @Mock
    PlanningEventService planningEventService;

    @Mock
    TravelService travelService;

    @Mock
    UserRight userRight;

    @Mock
    ModelMapper mapper;

    User currentUser;
    User notCurrentUser;
    Travel travel;
    PlanningEvent planningEvent;
    PlanningEvent planningEventToCreate;

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
        travel.setCreatedBy(currentUser);
        planningEvent = new PlanningEvent();
        planningEvent.setId(PLANNING_EVENT_ID);
        planningEvent.setName(PIN_NAME);
        planningEvent.setCreatedBy(currentUser);
        planningEventToCreate = new PlanningEvent();
        planningEventToCreate.setName(PIN_NAME);
        planningEventToCreate.setCreatedBy(currentUser);
        planningEventToCreate.setTravel(travel);
        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void shouldReturnRequiredEntity_onGetPin_whenCurrentUserHasReadRight() throws PinNotFoundException, CurrentUserAuthorizationException {
        //ARRANGE
        Pin pin = buildPin();
        GetPinDto expectedGetPinDto = buildExpectedGetPinDto();
        when(pinRepository.findById(PIN_ID)).thenReturn(Optional.of(pin));
        when(mapper.map(pin, GetPinDto.class)).thenReturn(expectedGetPinDto);
        when(userRight.currentUserCanRead(travel)).thenReturn(true);

        //ACT
        GetPinDto result = pinService.getPinById(PIN_ID);

        //ASSERT
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedGetPinDto);
    }

    @Test
    void shouldThrowCUAException_onGetPin_whenCurrentUserHasNotReadRight() throws CurrentUserAuthorizationException {
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
    void shouldSaveNewEntity_onCreatePin_whenCurrentUserHasWriteRight() throws CurrentUserAuthorizationException, TravelNotFoundException, PinAlreadyExistException {
        //ARRANGE
        CreatePinDto createPinDto = buildCreatePinDto();
        Pin pinToSave = buildMappedPinFromCreateDto();
        CreatePlanningEventDto planningEventDtoToCreate = buildMappedCreatePlanningEventDtoFromPlanningEvent(pinToSave);
        Pin expectedCreatedPin = buildExpectedCreatedPin();
        expectedCreatedPin.getPlanningEvent().setPin(expectedCreatedPin);
        when(pinRepository.findById(PIN_ID)).thenReturn(Optional.empty());
        when(mapper.map(createPinDto, Pin.class)).thenReturn(pinToSave);
        when(travelService.getTravelById(TRAVEL_ID)).thenReturn(travel);
        when(mapper.map(any(PlanningEvent.class), any(Class.class))).thenReturn(planningEventDtoToCreate);
        when(userRight.currentUserCanWrite(TRAVEL_ID)).thenReturn(true);
        //ACT
        pinService.createPin(createPinDto);

        //ASSERT
        verify(pinRepository).save(pinCaptor.capture());
        Pin pinSaved = pinCaptor.getValue();
        assertThat(pinSaved).usingRecursiveComparison().isEqualTo(expectedCreatedPin);
    }

    @Test
    void shouldThrowPAEException_onCreatePin_whenForATravelAPinAlreadyExistsAtTheSamePosition() {
        //ARRANGE
        when(pinRepository.findByTravelIdAndLongitudeAndLatitude(TRAVEL_ID, LONGITUDE, LATITUDE)).thenReturn(Optional.of(new Pin()));

        //ACT
        //ASSERT
        assertThatExceptionOfType(PinAlreadyExistException.class)
                .isThrownBy(() -> pinService.createPin(buildCreatePinDto()))
                .withMessage(ExceptionMessage.PIN_ALREADY_EXIST);
    }

    @Test
    void shouldThrowCUAException_onCreatePin_whenUserCannotWriteOnThisTravel() throws CurrentUserAuthorizationException {
        //ARRANGE
        when(pinRepository.findByTravelIdAndLongitudeAndLatitude(TRAVEL_ID, LONGITUDE, LATITUDE)).thenReturn(Optional.empty());
        when(userRight.currentUserCanWrite(travel)).thenReturn(false);

        //ACT
        //ASSERT
        assertThatExceptionOfType(CurrentUserAuthorizationException.class)
                .isThrownBy(() -> pinService.createPin(buildCreatePinDto()))
                .withMessage(ExceptionMessage.CURRENT_USER_NOT_ALLOWED_TO_CREATE_PIN);
    }

    @Test
    void shouldSaveTheUpdatedEntity_onUpdatePin_whenCurrentUserHasWriteRight() throws CurrentUserAuthorizationException, PinNotFoundException {
        //ARRANGE
        UpdatePinDto updatePinDto = buildUpdatePinDto();
        when(pinRepository.findById(PIN_ID)).thenReturn(Optional.of(buildPin()));
        when(userRight.currentUserCanWrite(travel)).thenReturn(true);

        //ACT
        pinService.updatePin(updatePinDto);

        //ASSERT
        verify(pinRepository).save(pinCaptor.capture());
        Pin updatedPin = pinCaptor.getValue();
        assertThat(updatedPin).usingRecursiveComparison().isEqualTo(buildExpectedUpdatedPin());
    }

    @Test
    void shouldNotSaveTheUpdatedEntity_onUpdatePin_whenUpdatedPinNameIsSameAsCurrentPinName() throws CurrentUserAuthorizationException, PinNotFoundException {
        //ARRANGE
        UpdatePinDto updatePinDtoWithNotUpdatedName = buildUpdatePinDtoWithNotUpdatedName();
        when(pinRepository.findById(PIN_ID)).thenReturn(Optional.of(buildPin()));
        when(userRight.currentUserCanWrite(travel)).thenReturn(true);

        //ACT
        pinService.updatePin(updatePinDtoWithNotUpdatedName);

        //ASSERT
        verify(pinRepository, Mockito.times(0)).save(any(Pin.class));
    }

    @Test
    void shouldThrowCUAException_onUpdatePin_whenUserCannotWriteOnThisTravel() throws CurrentUserAuthorizationException {
        //ARRANGE
        UpdatePinDto updatePinDto = buildUpdatePinDto();
        when(pinRepository.findById(PIN_ID)).thenReturn(Optional.of(buildPin()));
        when(userRight.currentUserCanWrite(travel)).thenReturn(false);

        //ACT
        //ASSERT
        assertThatExceptionOfType(CurrentUserAuthorizationException.class)
                .isThrownBy(() -> pinService.updatePin(updatePinDto))
                .withMessage(ExceptionMessage.CURRENT_USER_NOT_ALLOWED_TO_UPDATE_PIN);
    }

    @Test
    void shouldThrowPNFException_onUpdatePin_whenPinToUpdateIsNotFound() throws CurrentUserAuthorizationException {
        //ARRANGE
        UpdatePinDto updatePinDto = buildUpdatePinDto();
        when(pinRepository.findById(PIN_ID)).thenReturn(Optional.empty());
        when(userRight.currentUserCanWrite(travel)).thenReturn(true);

        //ACT
        //ASSERT
        assertThatExceptionOfType(PinNotFoundException.class)
                .isThrownBy(() -> pinService.updatePin(updatePinDto))
                .withMessage(ExceptionMessage.PIN_NOT_FOUND);
    }

    @Test
    void shouldDeleteThePinAndLinkedPlanningEventEntity_onDeletePinById_whenCurrentUserHasWriteRight() throws CurrentUserAuthorizationException, PinNotFoundException {
        //ARRANGE
        when(pinRepository.findById(PIN_ID)).thenReturn(Optional.of(buildPin()));
        when(userRight.currentUserCanWrite(travel)).thenReturn(true);

        //ACT
        pinService.deletePinById(PIN_ID);

        //ASSERT
        verify(pinRepository).deleteById(idCaptor.capture());
        Long idOfPinToDelete = idCaptor.getValue();
        assertThat(idOfPinToDelete).isEqualTo(PIN_ID);
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
        getPinDto.setCreatedBy(currentUser.getId());
        getPinDto.setTravelId(travel.getId());
        getPinDto.setLatitude(LATITUDE);
        getPinDto.setLongitude(LONGITUDE);
        getPinDto.setPlanningEventId(planningEvent.getId());
        return getPinDto;
    }

    /* BUILDERS - CREATE */

    private CreatePinDto buildCreatePinDto() {
        CreatePinDto createPinDto = new CreatePinDto();
        createPinDto.setName(PIN_NAME);
        createPinDto.setTravelId(TRAVEL_ID);
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
        pin.setPlanningEvent(planningEventToCreate);
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

    /* BUILDERS - UPDATE */

    private UpdatePinDto buildUpdatePinDto() {
        UpdatePinDto updatePinDto = new UpdatePinDto();
        updatePinDto.setId(PIN_ID);
        updatePinDto.setName(UPDATED_PIN_NAME);
        return updatePinDto;
    }

    private UpdatePinDto buildUpdatePinDtoWithNotUpdatedName() {
        UpdatePinDto updatePinDto = new UpdatePinDto();
        updatePinDto.setId(PIN_ID);
        updatePinDto.setName(PIN_NAME);
        return updatePinDto;
    }

    private Pin buildExpectedUpdatedPin() {
        Pin updatedPin = new Pin();
        updatedPin.setId(PIN_ID);
        updatedPin.setName(UPDATED_PIN_NAME);
        updatedPin.setCreatedBy(currentUser);
        updatedPin.setTravel(travel);
        updatedPin.setLatitude(LATITUDE);
        updatedPin.setLongitude(LONGITUDE);
        updatedPin.setPlanningEvent(planningEvent);
        return updatedPin;
    }
}
