package com.plango.api.common.component;

import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.common.types.Role;
import com.plango.api.entity.Travel;
import com.plango.api.entity.User;
import com.plango.api.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserRight {
    @Autowired
    MemberService memberService;

    @Autowired
    IAuthenticationFacade authenticationFacade;

    public boolean currentUserCanRead(Travel travel) throws CurrentUserAuthorizationException {
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
}
