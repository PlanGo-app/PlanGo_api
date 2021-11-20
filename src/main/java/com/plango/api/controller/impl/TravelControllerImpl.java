package com.plango.api.controller.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jayway.jsonpath.spi.mapper.MappingException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.controller.TravelController;
import com.plango.api.dto.TravelDto;
import com.plango.api.entity.Travel;
import com.plango.api.security.UserAuthDetails;
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
    ModelMapper modelMapper;

    /**
     * User create a travel and is added to member list
     * @param newTravelInfo travel information
     * @return : CREATED, or else : exception message, or : user authenticated not found
     */
    @Override
    public ResponseEntity<String> createTravel(TravelDto newTravelInfo) {
        try {
            Travel newTravel = modelMapper.map(newTravelInfo, Travel.class);
            travelService.createTravel(newTravel);
            return new ResponseEntity<>(
                    String.format("New travel in %s, %s created.", newTravel.getCity(), newTravel.getCountry()),
                    HttpStatus.CREATED);
        } catch (IllegalArgumentException | MappingException e) {
            return new ResponseEntity<>("Couldn't create travel because of missing or wrong informations.", HttpStatus.BAD_REQUEST);
        }
        catch (UserNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    /**
     * Send all user's travels information
     * @return List<TravelDto> travels information, or else : user authenticated not found
     */
    @Override
    public ResponseEntity<List<TravelDto>> getTravels() {
        try {
            List<Travel> travelsOfUser = travelService.getTravelsOfCurrentUser();
            List<TravelDto> travels = travelsOfUser.stream().map(travel -> modelMapper.map(travel, TravelDto.class)).collect(Collectors.toList());
            return new ResponseEntity<>(travels, HttpStatus.OK);
        }
        catch (UserNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
