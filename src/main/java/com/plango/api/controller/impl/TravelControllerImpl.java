package com.plango.api.controller.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.jayway.jsonpath.spi.mapper.MappingException;
import com.plango.api.common.exception.TravelNotFoundException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.common.types.Role;
import com.plango.api.controller.TravelController;
import com.plango.api.dto.TravelDto;
import com.plango.api.dto.UserBaseDto;
import com.plango.api.dto.UserDto;
import com.plango.api.entity.Travel;

import com.plango.api.entity.User;
import com.plango.api.service.TravelService;
import com.plango.api.service.UserService;
import org.modelmapper.ModelMapper;
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

    @Autowired
    ModelMapper modelMapper;

    /**
     * User create a travel and is added to member list
     * @param newTravelInfo travel information
     * @return : CREATED, or else : exception message, or : user authenticated not found
     */
    @Override
    public ResponseEntity<String> createTravel(TravelDto newTravelInfo) {
        try {
            travelService.createTravel(this.convertToEntity(newTravelInfo));
            return new ResponseEntity<>(
                    "New travel created.",
                    HttpStatus.CREATED);
        } catch (IllegalArgumentException | MappingException e) {
            return new ResponseEntity<>("Couldn't create travel because of missing or wrong informations.", HttpStatus.BAD_REQUEST);
        }
        catch (UserNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    @Override
    public ResponseEntity<String> addMemberToTravel(Long travelId, Long userId, Role role) {
        try {
            Travel travel = travelService.getTravelById(travelId);
            User user = userService.getUserById(userId);
            travelService.addMember(travel, user, role);
            return new ResponseEntity<>("New member added to travel.", HttpStatus.OK);
        }
        catch (TravelNotFoundException | UserNotFoundException e) {
            return new ResponseEntity<>("Couldn't add user to travel because travel or user were not found.", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<List<UserDto>> getTravelMembers(Long travelId) {
        return null;
    }

    private TravelDto convertToDto(Travel travel) {
        return modelMapper.map(travel, TravelDto.class);
    }

    private Travel convertToEntity(TravelDto travelDto) {
        return modelMapper.map(travelDto, Travel.class);
    }


}
