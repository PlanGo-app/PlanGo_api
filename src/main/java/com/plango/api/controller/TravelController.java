package com.plango.api.controller;

import com.plango.api.dto.TravelDto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/travel")
public interface TravelController {

    @PostMapping("")
    ResponseEntity<String> createTravel(@RequestBody TravelDto newTravel);
}
