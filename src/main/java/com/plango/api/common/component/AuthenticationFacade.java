package com.plango.api.common.component;

import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.security.UserAuthDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade implements IAuthenticationFacade {

    @Override
    public UserAuthDetails getCurrentUserAuthDetails() throws CurrentUserAuthorizationException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object userAuth = auth.getPrincipal();
        if(userAuth instanceof UserAuthDetails userAuthDetails) {
            return userAuthDetails;
        } else {
            throw new CurrentUserAuthorizationException("Could not find current user");
        }
    }
}
