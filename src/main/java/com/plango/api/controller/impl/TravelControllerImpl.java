package com.plango.api.controller.impl;

import javax.naming.ConfigurationException;

import com.jayway.jsonpath.spi.mapper.MappingException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TravelControllerImpl implements TravelController {

    @Autowired
    TravelService travelService;

    @Autowired
    UserService userService;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public ResponseEntity<String> createTravel(TravelDto newTravelInfo) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserAuthDetails userAuth = (UserAuthDetails) auth.getPrincipal();

        if (userAuth != null) {
            try {
                Travel newTravel = modelMapper.map(newTravelInfo, Travel.class);
                newTravel.setCreatedBy(userService.getUserByPseudo(userAuth.getUsername()));
                travelService.addTravel(newTravel);
                return new ResponseEntity<>(
                        String.format("New travel in %s,%s created.", newTravel.getCity(), newTravel.getCountry()),
                        HttpStatus.CREATED);
            } catch (IllegalArgumentException | MappingException e) {
                return new ResponseEntity<>(
                        "Couldn't create travel because of missing informations.", 
                        HttpStatus.BAD_REQUEST);
            }
        }
        
        return new ResponseEntity<>("No user authenticated", HttpStatus.BAD_REQUEST);
    }
}
