package com.plango.api.service;

import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.entity.User;

public interface UserService {
    void createUser(User user);
    void updateUser(User user) throws UserNotFoundException;
    User getUserById(Long id) throws UserNotFoundException;
    void deleteUser(Long id) throws UserNotFoundException;
    boolean pseudoTaken(String pseudo);
    boolean emailTaken(String email);
}
