package com.plango.api.service;

import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.common.exception.UserAlreadyExistsException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.entity.User;

public interface UserService {
    void createUser(User user) throws UserAlreadyExistsException;
    void updateUser(Long id, User user) throws UserNotFoundException, CurrentUserAuthorizationException;
    User getUserById(Long id) throws UserNotFoundException;
    User getUserByPseudo(String pseudo) throws UserNotFoundException;
    void deleteUser(Long id) throws UserNotFoundException, CurrentUserAuthorizationException;
}
