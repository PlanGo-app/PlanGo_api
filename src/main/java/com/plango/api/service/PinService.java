package com.plango.api.service;

import com.plango.api.common.component.IAuthenticationFacade;
import com.plango.api.common.component.UserRight;
import com.plango.api.common.constant.ExceptionMessage;
import com.plango.api.common.exception.*;
import com.plango.api.common.mapper.CustMapper;
import com.plango.api.dto.pin.CreatePinDto;
import com.plango.api.dto.pin.GetPinDto;
import com.plango.api.dto.pin.UpdatePinDto;
import com.plango.api.entity.Pin;
import com.plango.api.entity.PlanningEvent;
import com.plango.api.entity.Travel;
import com.plango.api.repository.PinRepository;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
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
    TravelService travelService;

    @Autowired
    UserRight userRight;

    @Autowired
    ModelMapper mapper;

    private CustMapper custMapper = Mappers.getMapper(CustMapper.class);

    public GetPinDto getPinById(Long id) throws CurrentUserAuthorizationException, PinNotFoundException {
        Pin pin = findPinById(id);
        if(!userRight.currentUserCanRead(pin.getTravel())) {
            throw new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_NOT_ALLOWED_TO_GET_PIN);
        }
        return custMapper.pinToGetPinDto(pin);
    }

    public GetPinDto getPinByTravelAndCoordinates(Long travelId, Float longitude, Float latitude) throws PinNotFoundException, TravelNotFoundException, CurrentUserAuthorizationException {
        if(!userRight.currentUserCanRead(travelId)) {
            throw new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_NOT_ALLOWED_TO_GET_PIN);
        }
        Pin pin = pinRepository.findByTravelIdAndLongitudeAndLatitude(travelId, longitude, latitude).orElse(null);
        if(pin == null) {
            throw new PinNotFoundException(ExceptionMessage.PIN_NOT_FOUND);
        }
        return custMapper.pinToGetPinDto(pin);
    }

    public void createPin(CreatePinDto createPinDto) throws PinAlreadyExistException, TravelNotFoundException, CurrentUserAuthorizationException {
        Pin pinToCheckIfExist = pinRepository.findByTravelIdAndLongitudeAndLatitude(createPinDto.getTravelId(), createPinDto.getLongitude(), createPinDto.getLatitude()).orElse(null);
        if(pinToCheckIfExist != null) {
            throw new PinAlreadyExistException(ExceptionMessage.PIN_ALREADY_EXIST);
        }
        if(!userRight.currentUserCanWrite(createPinDto.getTravelId())) {
            throw new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_NOT_ALLOWED_TO_CREATE_PIN);
        }
        Pin pin = mapper.map(createPinDto, Pin.class);
        pin.setCreatedBy(authenticationFacade.getCurrentUser());
        pin.setId(null);
        Travel travel = travelService.getTravelById(createPinDto.getTravelId());
        PlanningEvent planningEvent = new PlanningEvent();
        planningEvent.setPin(pin);
        planningEvent.setTravel(travel);
        planningEvent.setCreatedBy(authenticationFacade.getCurrentUser());
        planningEvent.setName(pin.getName());

        pin.setPlanningEvent(planningEvent);
        pinRepository.save(pin);
    }

    public void updatePin(UpdatePinDto updatePinDto) throws PinNotFoundException, CurrentUserAuthorizationException {
        Pin pinToUpdate = findPinById(updatePinDto.getId());
        if(!pinToUpdate.getName().equals(updatePinDto.getName())) {
            if(!userRight.currentUserCanWrite(pinToUpdate.getTravel())) {
                throw new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_NOT_ALLOWED_TO_UPDATE_PIN);
            }
            pinToUpdate.setName(updatePinDto.getName());
            PlanningEvent pinPlanningEvent = pinToUpdate.getPlanningEvent();
            pinPlanningEvent.setName(pinToUpdate.getName());
            pinRepository.save(pinToUpdate);
        }
    }

    public void deletePinById(Long id) throws PinNotFoundException, CurrentUserAuthorizationException {
        Pin pinToDelete = findPinById(id);
        if(!userRight.currentUserCanWrite(pinToDelete.getTravel())) {
            throw new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_NOT_ALLOWED_TO_DELETE_PIN);
        }
        pinRepository.deleteById(id);
    }

    public List<GetPinDto> getPinByTravel(Travel travel) {
        List<Pin> pinList = pinRepository.findAllByTravel(travel);
        return pinList
                .stream()
                .map(pin -> custMapper.pinToGetPinDto(pin))
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
