package com.plango.api.controller;

import com.plango.api.dto.TravelDto;

import com.plango.api.dto.TravelMembersDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/travel")
public interface TravelController {

    @PostMapping("")
    ResponseEntity<String> createTravel(@RequestBody TravelDto newTravel);

    @PostMapping("/{travelId}/member/{userId}")
    ResponseEntity<String> addMemberToTravel(@PathVariable Long travelId, @PathVariable Long userId, @RequestParam String role);

    @PutMapping("/{travelId}/member/{userId}")
    ResponseEntity<String> updateMemberOfTravel(@PathVariable Long travelId, @PathVariable Long userId, @RequestParam String role);

    @DeleteMapping("/{travelId}/member/{userId}")
    ResponseEntity<String> deleteMemberOfTravel(@PathVariable Long travelId, @PathVariable Long userId);

    @GetMapping("/{travelId}/members")
    ResponseEntity<TravelMembersDto> getTravelMembers(@PathVariable Long travelId);

    @GetMapping("/invitation")
    ResponseEntity<TravelDto> getTravelWithInvitation(@RequestParam String code);
}
