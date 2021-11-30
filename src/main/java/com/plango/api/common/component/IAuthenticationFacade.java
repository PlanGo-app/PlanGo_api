package com.plango.api.common.component;

import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.entity.User;
import com.plango.api.security.UserAuthDetails;

public interface IAuthenticationFacade {
    UserAuthDetails getCurrentUserAuthDetails() throws CurrentUserAuthorizationException;
    User getCurrentUser() throws CurrentUserAuthorizationException;
}
