package com.plango.api.service;

import com.plango.api.common.component.IAuthenticationFacade;
import com.plango.api.common.constant.ExceptionMessage;
import com.plango.api.common.exception.*;
import com.plango.api.common.types.Role;
import com.plango.api.dto.planningevent.CreatePlanningEventDto;
import com.plango.api.dto.planningevent.GetPlanningEventDto;
import com.plango.api.dto.planningevent.PlanningEventDto;
import com.plango.api.dto.planningevent.UpdatePlanningEventDto;
import com.plango.api.entity.PlanningEvent;
import com.plango.api.entity.User;
import com.plango.api.repository.PlanningEventRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanningEventService {
    @Autowired
    PlanningEventRepository planningEventRepository;

    @Autowired
    TravelService travelService;

    @Autowired
    MemberService memberService;

    @Autowired
    IAuthenticationFacade authenticationFacade;

    @Autowired
    ModelMapper mapper;

    public GetPlanningEventDto getPlanningEventById(Long id) throws PlanningEventNotFoundException, CurrentUserAuthorizationException {
        PlanningEvent planningEvent = findPlanningEventById(id);
        if(!userCanRead(planningEvent)) {
            throw new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_NOT_ALLOWED_TO_GET_PLANNING_EVENT);
        }
        return mapper.map(planningEvent, GetPlanningEventDto.class);
    }

    public void createPlanningEvent(CreatePlanningEventDto createPlanningEventDto) throws UserNotFoundException, TravelNotFoundException, InvalidRequestDataException {
        if(endDateIsBeforeStartDate(createPlanningEventDto)) {
            throw new InvalidRequestDataException(ExceptionMessage.DATE_START_SHOULD_BE_BEFORE_DATE_END);
        } else if (createPlanningEventDto.getPin() == null) {
            throw new InvalidRequestDataException(ExceptionMessage.PLANNING_EVENT_MUST_BE_LINKED_TO_PIN);
        }
        travelService.getTravelById(createPlanningEventDto.getTravel().getId());
        PlanningEvent planningEvent = mapper.map(createPlanningEventDto, PlanningEvent.class);
        planningEvent.setCreatedBy(authenticationFacade.getCurrentUser());
        planningEventRepository.save(planningEvent);
    }

    public void updatePlanningEvent(UpdatePlanningEventDto updatePlanningEventDto) throws PlanningEventNotFoundException, CurrentUserAuthorizationException, InvalidRequestDataException {
        if(endDateIsBeforeStartDate(updatePlanningEventDto)) {
            throw new InvalidRequestDataException(ExceptionMessage.DATE_START_SHOULD_BE_BEFORE_DATE_END);
        }
        PlanningEvent planningEventOnUpdate = findPlanningEventById(updatePlanningEventDto.getId());
        if(!userCanWrite(planningEventOnUpdate)) {
            throw new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_NOT_ALLOWED_TO_UPDATE_PLANNING_EVENT);
        }
        planningEventOnUpdate.setName(updatePlanningEventDto.getName());
        planningEventOnUpdate.setDateStart(updatePlanningEventDto.getDateStart());
        planningEventOnUpdate.setDateEnd(updatePlanningEventDto.getDateEnd());
        planningEventOnUpdate.setEventAfter(updatePlanningEventDto.getEventAfter());
        planningEventOnUpdate.setTransportTypeToNext(updatePlanningEventDto.getTransportTypeToNext());

        planningEventRepository.save(planningEventOnUpdate);
    }

    public void deletePlanningEventById(Long id) throws PlanningEventNotFoundException {
        findPlanningEventById(id);
        planningEventRepository.deleteById(id);
    }

    private PlanningEvent findPlanningEventById(Long id) throws PlanningEventNotFoundException {
        PlanningEvent planningEvent = planningEventRepository.findById(id).orElse(null);
        if(planningEvent == null) {
            throw new PlanningEventNotFoundException(ExceptionMessage.PLANNING_EVENT_NOT_FOUND);
        }
        return planningEvent;
    }

    private boolean endDateIsBeforeStartDate(PlanningEventDto planningEventDto) {
        return planningEventDto.getDateEnd().isBefore(planningEventDto.getDateStart());
    }

    private boolean userCanWrite(PlanningEvent planningEvent) throws CurrentUserAuthorizationException {
        try {
            User user = authenticationFacade.getCurrentUser();
            if(planningEvent.getCreatedBy().equals(user)) {
                return true;
            }
            Role currentUserRole = memberService.getMemberByTravel(planningEvent.getTravel(), user).getRole();
            if (currentUserRole.equals(Role.ADMIN) || currentUserRole.equals(Role.ORGANIZER)) {
                return true;
            } else {
                throw new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_NOT_ALLOWED_TO_UPDATE_PLANNING_EVENT);
            }
        } catch (UserNotFoundException e) {
            throw new CurrentUserAuthorizationException(e.getMessage());
        }
    }

    private boolean userCanRead(PlanningEvent planningEvent) throws CurrentUserAuthorizationException {
        try {
            User user = authenticationFacade.getCurrentUser();
            if(!memberService.isMember(user, planningEvent.getTravel())) {
                throw new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_NOT_ALLOWED_TO_GET_PLANNING_EVENT);
            }
        } catch (UserNotFoundException e) {
            throw new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_CANNOT_BE_AUTHENTICATED);
        }
        return true;
    }
}