package com.plango.api.controller;

import java.util.List;

import com.plango.api.dto.TravelDto;

import com.plango.api.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/travel")
public interface TravelController {

    @PostMapping("")
    ResponseEntity<String> createTravel(@RequestBody TravelDto newTravel);

    @PostMapping("/{id}/members")
    ResponseEntity<String> addMemberToTravel(@PathVariable Long travelId);

    @GetMapping("/{id}/members")
    ResponseEntity<List<UserDto>> getTravelMembers(@PathVariable Long travelId);
}
