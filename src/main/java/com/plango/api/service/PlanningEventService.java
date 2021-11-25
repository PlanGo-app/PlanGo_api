package com.plango.api.service;

import com.plango.api.common.component.IAuthenticationFacade;
import com.plango.api.common.constant.ExceptionMessage;
import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.common.exception.PlanningEventNotFoundException;
import com.plango.api.common.exception.TravelNotFoundException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.dto.planningevent.CreatePlanningEventDto;
import com.plango.api.dto.planningevent.GetPlanningEventDto;
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
            throw new CurrentUserAuthorizationException("Current user not authorized");
        }
        return mapper.map(planningEvent, GetPlanningEventDto.class);
    }

    public void createPlanningEvent(CreatePlanningEventDto createPlanningEventDto) throws UserNotFoundException, TravelNotFoundException {
        travelService.getTravelById(createPlanningEventDto.getTravel().getId());
        PlanningEvent planningEvent = mapper.map(createPlanningEventDto, PlanningEvent.class);
        planningEvent.setCreatedBy(authenticationFacade.getCurrentUser());
        planningEventRepository.save(planningEvent);
    }

    public void updatePlanningEvent(UpdatePlanningEventDto updatePlanningEventDto) throws PlanningEventNotFoundException, CurrentUserAuthorizationException {
        PlanningEvent planningEventOnUpdate = findPlanningEventById(updatePlanningEventDto.getId());
        if(!userCanWrite(planningEventOnUpdate)) {
            throw new CurrentUserAuthorizationException(String.format("Current User cannot update Planning event '%d'",planningEventOnUpdate.getId()));
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
            throw new PlanningEventNotFoundException(String.format("No planning event with id %d were found", id));
        }
        return planningEvent;
    }

    private boolean userCanWrite(PlanningEvent planningEvent) throws CurrentUserAuthorizationException {
        try {
            User user = authenticationFacade.getCurrentUser();
            if(!planningEvent.getCreatedBy().equals(user)) {
                throw new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_NOT_ALLOWED_TO_UPDATE_PLANNING_EVENT);
            }
        } catch (UserNotFoundException e) {
            throw new CurrentUserAuthorizationException("Issue will trying to authenticate current user");
        }
        return true;
    }

    private boolean userCanRead(PlanningEvent planningEvent) throws CurrentUserAuthorizationException {
        try {
            User user = authenticationFacade.getCurrentUser();
            if(!memberService.isMember(user, planningEvent.getTravel())) {
                throw new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_NOT_ALLOWED_TO_GET_PLANNING_EVENT);
            }
        } catch (UserNotFoundException e) {
            throw new CurrentUserAuthorizationException("Issue will trying to authenticate current user");
        }
        return true;
    }
}
