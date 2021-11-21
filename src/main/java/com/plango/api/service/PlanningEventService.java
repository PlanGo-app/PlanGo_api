package com.plango.api.service;

import com.plango.api.common.component.IAuthenticationFacade;
import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.entity.PlanningEvent;
import com.plango.api.repository.PlanningEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanningEventService {
    @Autowired
    PlanningEventRepository planningEventRepository;

    @Autowired
    IAuthenticationFacade authenticationFacade;

    public PlanningEvent getPlanningEventById(Long id) {
        if(!userHasRight(userOnUpdate, authenticationFacade.getCurrentUserAuthDetails())) {
            throw new CurrentUserAuthorizationException("Current user not authorized");
        }
    }
}
