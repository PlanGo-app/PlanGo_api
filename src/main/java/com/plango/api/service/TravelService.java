package com.plango.api.service;

import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.entity.Travel;

import java.util.List;

public interface TravelService {
    void createTravel(Travel newTravel) throws UserNotFoundException;
    List<Travel> getTravelsOfCurrentUser() throws UserNotFoundException;
}
