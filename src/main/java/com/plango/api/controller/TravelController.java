package com.plango.api.controller;

import com.plango.api.common.types.Role;
import com.plango.api.dto.TravelDto;

import com.plango.api.dto.TravelMembersDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/travel")
public interface TravelController {

    @PostMapping("")
    ResponseEntity<String> createTravel(@RequestBody TravelDto newTravel);

    @PostMapping("/{travelId}/member/{userId}")
    ResponseEntity<String> addMemberToTravel(@PathVariable Long travelId, @PathVariable Long userId, @RequestBody Role role);

    @GetMapping("/{travelId}/members")
    ResponseEntity<TravelMembersDto> getTravelMembers(@PathVariable Long travelId);
}
