package com.plango.api.service;

import com.plango.api.common.component.IAuthenticationFacade;
import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.common.types.Role;
import com.plango.api.entity.MemberList;
import com.plango.api.entity.Travel;
import com.plango.api.entity.User;
import com.plango.api.repository.MemberListRepository;
import com.plango.api.repository.TravelRepository;
import com.plango.api.security.UserAuthDetails;

import com.plango.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TravelService {

    @Autowired
    TravelRepository travelRepository;

    @Autowired
    MemberListRepository memberListRepository;

    @Autowired
    UserService userService;

    @Autowired
    IAuthenticationFacade authenticationFacade;

    public void createTravel(Travel newTravel) throws UserNotFoundException {
        User currentUser = this.getCurrentUser();
        newTravel.setCreatedBy(currentUser);
        Travel travel = travelRepository.save(newTravel);

        MemberList creatorMember = new MemberList();
        creatorMember.setMember(currentUser);
        creatorMember.setTravel(travel);
        creatorMember.setRole(Role.ADMIN);
        memberListRepository.save(creatorMember);
    }

    public List<Travel> getTravelsOfCurrentUser(Long id) throws UserNotFoundException {
        User currentUser = userService.getUserById(id);
        List<MemberList> listParticipations = memberListRepository.findAllByMember(currentUser);
        List<Travel> listTravels = new ArrayList<>();
        for(MemberList listParticipation : listParticipations){
            listTravels.add(listParticipation.getTravel());
        }
        return listTravels;
    }

    private User getCurrentUser() throws UserNotFoundException {
        try {
            UserAuthDetails currentUserAuth = authenticationFacade.getCurrentUserAuthDetails();
            return userService.getUserByPseudo(currentUserAuth.getUsername());
        }
        catch(CurrentUserAuthorizationException | UserNotFoundException e){
            throw new UserNotFoundException("Couldn't find current user.");
        }
    }

}
