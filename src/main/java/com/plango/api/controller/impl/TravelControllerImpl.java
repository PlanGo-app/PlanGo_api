package com.plango.api.controller.impl;


import com.jayway.jsonpath.spi.mapper.MappingException;
import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.common.exception.TravelNotFoundException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.common.types.Role;
import com.plango.api.controller.TravelController;
import com.plango.api.dto.travel.CreateTravelDto;
import com.plango.api.dto.travel.GetTravelDto;
import com.plango.api.dto.member.TravelMembersDto;
import com.plango.api.entity.Travel;

import com.plango.api.entity.User;
import com.plango.api.service.TravelService;
import com.plango.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TravelControllerImpl implements TravelController {

    @Autowired
    TravelService travelService;

    @Autowired
    UserService userService;

    /**
     * User create a travel and is added to member list
     * @param newTravelInfo travel information
     * @return : CREATED, or else : exception message, or : user authenticated not found
     */
    @Override
    public ResponseEntity<String> createTravel(CreateTravelDto newTravelInfo) {
        try {
            travelService.createTravel(travelService.convertCreateDtoToEntity(newTravelInfo));
            return new ResponseEntity<>(
                    "New travel created.",
                    HttpStatus.CREATED);
        } catch (IllegalArgumentException | MappingException e) {
            return new ResponseEntity<>("Couldn't create travel because of missing or wrong informations.", HttpStatus.BAD_REQUEST);
        }
        catch (UserNotFoundException | CurrentUserAuthorizationException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    @Override
    public ResponseEntity<String> addMemberToTravel(Long travelId, Long userId, String role) {
        try {
            Travel travel = travelService.getTravelById(travelId);
            User user = userService.getUserById(userId);
            if(role.equals("ADMIN") || role.equals("ORGANIZER") || role.equals("OBSERVER")) {
                travelService.addMember(travel, user, Role.valueOf(role));
                return new ResponseEntity<>("New member added to travel.", HttpStatus.CREATED);
            }
            else {
                return new ResponseEntity<>(String.format("Couldn't grant role %s which doesn't exist.", role), HttpStatus.BAD_REQUEST);
            }
        }
        catch (TravelNotFoundException | UserNotFoundException e) {
            return new ResponseEntity<>("Couldn't add user to travel because travel or user were not found.", HttpStatus.NOT_FOUND);
        }
        catch(CurrentUserAuthorizationException e){
            return new ResponseEntity<>("Current user not authorized to add a member to current travel.", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public ResponseEntity<String> updateMemberOfTravel(Long travelId, Long userId, String role) {
        try {
            Travel travel = travelService.getTravelById(travelId);
            User user = userService.getUserById(userId);
            if(role.equals("ADMIN") || role.equals("ORGANIZER") || role.equals("OBSERVER")) {
                travelService.updateMember(travel, user, Role.valueOf(role));
                return new ResponseEntity<>(String.format("Member updated with %s role.", role), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(String.format("Couldn't grant role %s which doesn't exist.", role), HttpStatus.BAD_REQUEST);
            }
        }
        catch (TravelNotFoundException | UserNotFoundException e) {
            return new ResponseEntity<>("Couldn't modify travel's member because travel or user were not found.", HttpStatus.NOT_FOUND);
        }
        catch(CurrentUserAuthorizationException e){
            return new ResponseEntity<>("Current user not authorized to modify a member's role for current travel.", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public ResponseEntity<String> deleteMemberOfTravel(Long travelId, Long userId) {
        try {
            Travel travel = travelService.getTravelById(travelId);
            User user = userService.getUserById(userId);
            travelService.deleteMember(travel, user);
            return new ResponseEntity<>("Member deleted.", HttpStatus.OK);
        }
        catch (TravelNotFoundException | UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch(CurrentUserAuthorizationException e){
            return new ResponseEntity<>("Current user not authorized to delete a member for current travel.", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public ResponseEntity<TravelMembersDto> getTravelMembers(Long travelId) {
        try {
            TravelMembersDto members = travelService.getMembers(travelId);
            return new ResponseEntity<>(members, HttpStatus.OK);
        }
        catch(TravelNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<GetTravelDto> getTravelWithInvitation(String code) {
        try {
            GetTravelDto travelFound = travelService.getTravelByInvitationCode(code);
            return new ResponseEntity<>(travelFound, HttpStatus.OK);
        }
        catch(TravelNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
