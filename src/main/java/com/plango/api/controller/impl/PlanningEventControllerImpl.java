package com.plango.api.controller.impl;

import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.common.exception.InvalidRequestDataException;
import com.plango.api.common.exception.PlanningEventNotFoundException;
import com.plango.api.controller.PlanningEventController;
import com.plango.api.dto.planningevent.GetPlanningEventDto;
import com.plango.api.dto.planningevent.UpdatePlanningEventDto;
import com.plango.api.service.PlanningEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PlanningEventControllerImpl implements PlanningEventController {

    @Autowired
    PlanningEventService planningEventService;

    @Override
    public ResponseEntity<GetPlanningEventDto> getPlanningEventById(Long id) {
        try {
            GetPlanningEventDto getPlanningEventDto = planningEventService.getPlanningEventById(id);
            return ResponseEntity.ok(getPlanningEventDto);
        } catch (PlanningEventNotFoundException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (CurrentUserAuthorizationException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public ResponseEntity<String> updatePlanningEvent(UpdatePlanningEventDto updatePlanningEventDto) {
        try {
            planningEventService.updatePlanningEvent(updatePlanningEventDto);
            return ResponseEntity.ok("Planning event updated");
        } catch (PlanningEventNotFoundException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (CurrentUserAuthorizationException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (InvalidRequestDataException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
