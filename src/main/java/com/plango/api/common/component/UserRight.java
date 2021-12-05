package com.plango.api.common.component;

import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.common.exception.TravelNotFoundException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.common.types.Role;
import com.plango.api.entity.Travel;
import com.plango.api.entity.User;
import com.plango.api.service.MemberService;
import com.plango.api.service.TravelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserRight {
    @Autowired
    MemberService memberService;

    @Autowired
    TravelService travelService;

    @Autowired
    IAuthenticationFacade authenticationFacade;

    public boolean currentUserCanRead(Travel travel) throws CurrentUserAuthorizationException {
        User user = authenticationFacade.getCurrentUser();
        return memberService.isMember(user, travel);
    }

    public boolean currentUserCanRead(Long travelId) throws CurrentUserAuthorizationException, TravelNotFoundException {
        Travel travel = travelService.getTravelById(travelId);
        User user = authenticationFacade.getCurrentUser();
        return memberService.isMember(user, travel);
    }

    public boolean currentUserCanWrite(Travel travel) throws CurrentUserAuthorizationException {
        try {
            User user = authenticationFacade.getCurrentUser();
            Role currentUserRole = memberService.getMemberByTravel(travel, user).getRole();
            return currentUserRole.equals(Role.ADMIN) || currentUserRole.equals(Role.ORGANIZER);
        } catch (UserNotFoundException e) {
            throw new CurrentUserAuthorizationException(e.getMessage());
        }
    }

    public boolean currentUserCanWrite(Long travelId) throws CurrentUserAuthorizationException {
        try {
            Travel travel = travelService.getTravelById(travelId);
            User user = authenticationFacade.getCurrentUser();
            Role currentUserRole = memberService.getMemberByTravel(travel, user).getRole();
            return currentUserRole.equals(Role.ADMIN) || currentUserRole.equals(Role.ORGANIZER);
        } catch (UserNotFoundException | TravelNotFoundException e) {
            throw new CurrentUserAuthorizationException(e.getMessage());
        }
    }

    public boolean currentUserCanDeleteTravel(Long travelId) throws CurrentUserAuthorizationException {
        try {
            Travel travel = travelService.getTravelById(travelId);
            User user = authenticationFacade.getCurrentUser();
            Role currentUserRole = memberService.getMemberByTravel(travel, user).getRole();
            return currentUserRole.equals(Role.ADMIN);
        } catch (UserNotFoundException | TravelNotFoundException e) {
            throw new CurrentUserAuthorizationException(e.getMessage());
        }
    }
}
