package com.plango.api.common.component;

import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.entity.User;
import com.plango.api.security.UserAuthDetails;
import com.plango.api.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade implements IAuthenticationFacade {

    @Autowired
    UserService userService;

    @Override
    public UserAuthDetails getCurrentUserAuthDetails() throws CurrentUserAuthorizationException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userAuth = auth.getPrincipal();
        if(userAuth instanceof UserAuthDetails) {
            return (UserAuthDetails) userAuth;
        } else {
            throw new CurrentUserAuthorizationException("Could not find current user");
        }
    }

    public User getCurrentUser() throws UserNotFoundException {
        try {
            UserAuthDetails currentUserAuth = this.getCurrentUserAuthDetails();
            return userService.getUserByPseudo(currentUserAuth.getUsername());
        }
        catch(CurrentUserAuthorizationException | UserNotFoundException e){
            throw new UserNotFoundException("Couldn't find current user.");
        }
    }
}
