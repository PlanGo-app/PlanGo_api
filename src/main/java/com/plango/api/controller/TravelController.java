package com.plango.api.controller;

import com.plango.api.dto.travel.CreateTravelDto;
import com.plango.api.dto.travel.GetTravelDto;
import com.plango.api.dto.TravelPlanningEventDto;
import com.plango.api.dto.member.TravelMembersDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/travel")
public interface TravelController {

    @PostMapping("")
    ResponseEntity<String> createTravel(@RequestBody CreateTravelDto newTravel);

    @PostMapping("/{travelId}/member/{userId}")
    ResponseEntity<String> addMemberToTravel(@PathVariable Long travelId, @PathVariable Long userId, @RequestParam String role);

    @PutMapping("/{travelId}/member/{userId}")
    ResponseEntity<String> updateMemberOfTravel(@PathVariable Long travelId, @PathVariable Long userId, @RequestParam String role);

    @DeleteMapping("/{travelId}/member/{userId}")
    ResponseEntity<String> deleteMemberOfTravel(@PathVariable Long travelId, @PathVariable Long userId);

    @GetMapping("/{travelId}/members")
    ResponseEntity<TravelMembersDto> getTravelMembers(@PathVariable Long travelId);

    @PostMapping("/invitation")
    ResponseEntity<GetTravelDto> addMemberToTravelWithInvitation(@RequestParam String code);

    @GetMapping("{travelId}/planningEvents")
    ResponseEntity<TravelPlanningEventDto> getTravelPlanningEvents(@PathVariable Long travelId);
}
