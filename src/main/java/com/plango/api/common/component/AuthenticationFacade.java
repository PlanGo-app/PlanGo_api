package com.plango.api.common.component;

import com.plango.api.common.constant.ExceptionMessage;
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
            throw new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_CANNOT_BE_AUTHENTICATED);
        }
    }

    public User getCurrentUser() throws CurrentUserAuthorizationException {
        try {
            UserAuthDetails currentUserAuth = this.getCurrentUserAuthDetails();
            return userService.getUserByPseudo(currentUserAuth.getUsername());
        }
        catch(UserNotFoundException e){
            throw new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_CANNOT_BE_AUTHENTICATED);
        }
    }
}
