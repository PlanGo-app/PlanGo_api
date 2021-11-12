package com.plango.api.service;

import com.plango.api.entity.User;

public interface UserService {
    void addUser(User user) throws Exception;
    void updateUser(User user);
    User getUserById(Long id);
    boolean pseudoTaken(String pseudo);
    boolean emailTaken(String email);
}
