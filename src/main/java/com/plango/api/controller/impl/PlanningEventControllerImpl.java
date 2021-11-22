package com.plango.api.controller.impl;

import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.common.exception.PlanningEventNotFoundException;
import com.plango.api.controller.PlanningEventController;
import com.plango.api.entity.PlanningEvent;
import com.plango.api.service.PlanningEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlanningEventControllerImpl implements PlanningEventController {

    @Autowired
    PlanningEventService planningEventService;

    @Override
    public ResponseEntity<PlanningEvent> getPlanningEventById(Long id) {
        try {
            planningEventService.getPlanningEventById(id);
        } catch (PlanningEventNotFoundException e) {
            e.printStackTrace();
        } catch (CurrentUserAuthorizationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<String> updatePlanningEvent(PlanningEvent planningEvent) {
        return null;
    }
}
