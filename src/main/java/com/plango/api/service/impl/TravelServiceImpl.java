package com.plango.api.service.impl;

import com.plango.api.entity.Travel;
import com.plango.api.repository.TravelRepository;
import com.plango.api.service.TravelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TravelServiceImpl implements TravelService {

    @Autowired
    TravelRepository travelRepository;

    @Override
    public void addTravel(Travel newTravel) {
        travelRepository.save(newTravel);
    }

}
