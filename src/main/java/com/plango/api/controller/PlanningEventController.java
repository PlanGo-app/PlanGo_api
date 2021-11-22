package com.plango.api.controller;

import com.plango.api.entity.PlanningEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/planning_event")
public interface PlanningEventController {
    @GetMapping(path = "/{id}")
    ResponseEntity<PlanningEvent> getPlanningEventById(@PathVariable Long id);

    @PutMapping(path = "/{id}", consumes="application/json")
    ResponseEntity<String> updatePlanningEvent(@RequestBody PlanningEvent planningEvent);
}
