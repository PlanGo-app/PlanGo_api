package com.plango.api.controller;

import com.plango.api.entity.PlanningEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/planning_event")
public interface PlanningEventController {
    @GetMapping(path = "/{id}")
    ResponseEntity<PlanningEvent> getPlanningEventById(@PathVariable Long id);
}
