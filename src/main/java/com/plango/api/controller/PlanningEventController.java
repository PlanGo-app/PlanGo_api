package com.plango.api.controller;

import com.plango.api.dto.planningevent.GetPlanningEventDto;
import com.plango.api.dto.planningevent.UpdatePlanningEventDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/planning_event")
public interface PlanningEventController {
    @GetMapping(path = "/{id}")
    ResponseEntity<GetPlanningEventDto> getPlanningEventById(@PathVariable Long id);

    @PutMapping(path = "/{id}", consumes="application/json")
    ResponseEntity<String> updatePlanningEvent(@RequestBody UpdatePlanningEventDto updatePlanningEventDto);
}
