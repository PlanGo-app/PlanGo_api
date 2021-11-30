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
import com.plango.api.repository.PinRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PinService {
    @Autowired
    PinRepository pinRepository;

    @Autowired
    IAuthenticationFacade authenticationFacade;

    @Autowired
    PlanningEventService planningEventService;

    @Autowired
    UserRight userRight;

    @Autowired
    ModelMapper mapper;

    public GetPinDto getPinById(Long id) throws CurrentUserAuthorizationException, PinNotFoundException {
        Pin pin = findPinById(id);
        if(!userRight.currentUserCanRead(pin.getTravel())) {
            throw new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_NOT_ALLOWED_TO_GET_PIN);
        }
        return mapper.map(pin, GetPinDto.class);
    }

    public void createPin(CreatePinDto createPinDto) throws PinAlreadyExistException, UserNotFoundException, TravelNotFoundException, InvalidRequestDataException, CurrentUserAuthorizationException {
        Pin pinToCheckIfExist = pinRepository.findByTravelAndLongitudeAndLatitude(createPinDto.getTravel(), createPinDto.getLongitude(), createPinDto.getLatitude()).orElse(null);
        if(pinToCheckIfExist != null) {
            throw new PinAlreadyExistException(ExceptionMessage.PIN_ALREADY_EXIST);
        }
        if(!userRight.currentUserCanWrite(createPinDto.getTravel())) {
            throw new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_NOT_ALLOWED_TO_CREATE_PIN);
        }
        Pin pin = mapper.map(createPinDto, Pin.class);
        pin.setCreatedBy(authenticationFacade.getCurrentUser());

        PlanningEvent planningEvent = new PlanningEvent();
        planningEvent.setPin(pin);
        planningEvent.setCreatedBy(authenticationFacade.getCurrentUser());
        planningEvent.setName(pin.getName());

        pin.setPlanningEvent(planningEvent);
        planningEventService.createPlanningEvent(mapper.map(planningEvent, CreatePlanningEventDto.class));
        pinRepository.save(pin);
    }

    public void updatePin(UpdatePinDto updatePinDto) throws PinNotFoundException {
        Pin pinToUpdate = findPinById(updatePinDto.getId());
        pinToUpdate.setName(updatePinDto.getName());
        if(!pinToUpdate.getName().equals(updatePinDto.getName())) {
            pinRepository.save(pinToUpdate);
        }
    }

    public void deletePinById(Long id) throws PinNotFoundException {
        Pin pinToDel = findPinById(id);
        Long planningEventId = pinToDel.getPlanningEvent().getId();
        try {
            planningEventService.getPlanningEventById(planningEventId);
            planningEventService.deletePlanningEventById(planningEventId);
        } catch (PlanningEventNotFoundException e) {
            log.error("Linked planning event not found to delete.");
        } catch (CurrentUserAuthorizationException e) {
            log.error("Authorisation issue trying to find linked planning event for deletion");
        }
        pinRepository.deleteById(id);
    }

    public List<GetPinDto> getPinByTravel(Travel travel) {
        List<Pin> pinList = pinRepository.findAllByTravel(travel);
        return pinList
                .stream()
                .map(pin -> mapper.map(pin, GetPinDto.class))
                .collect(Collectors.toList());
    }

    private Pin findPinById(Long id) throws PinNotFoundException {
        Pin pin = pinRepository.findById(id).orElse(null);
        if(pin == null) {
            throw new PinNotFoundException(ExceptionMessage.PIN_NOT_FOUND);
        }
        return pin;
    }
}
