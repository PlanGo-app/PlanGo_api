package com.plango.api.controller.impl;

import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.controller.PlanningEventController;
import com.plango.api.entity.PlanningEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlanningEventControllerImpl implements PlanningEventController {

    @Override
    public ResponseEntity<PlanningEvent> getPlanningEventById(Long id) {
        return null;
    }
}
