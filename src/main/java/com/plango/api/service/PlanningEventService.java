package com.plango.api.service;

import com.plango.api.common.component.IAuthenticationFacade;
import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.common.exception.PlanningEventNotFoundException;
import com.plango.api.entity.PlanningEvent;
import com.plango.api.repository.PlanningEventRepository;
import com.plango.api.security.UserAuthDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanningEventService {
    @Autowired
    PlanningEventRepository planningEventRepository;

    @Autowired
    IAuthenticationFacade authenticationFacade;

    public PlanningEvent getPlanningEventById(Long id) throws PlanningEventNotFoundException, CurrentUserAuthorizationException {
        PlanningEvent planningEvent = planningEventRepository.findById(id).orElse(null);
        if(planningEvent == null) {
            throw new PlanningEventNotFoundException(String.format("No planning event with id %d were found", id));
        }
        if(!userHasRight(planningEvent, authenticationFacade.getCurrentUserAuthDetails())) {
            throw new CurrentUserAuthorizationException("Current user not authorized");
        }
        return planningEvent;
    }

    public void createPlanningEvent(Long travelId, PlanningEvent planningEvent) {

    }

    private boolean userHasRight(PlanningEvent planningEvent, UserAuthDetails userAuthDetails) {
        return true;
    }
}
