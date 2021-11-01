package com.plango.api.service;

import com.plango.api.entity.User;

public interface UserService {
    void addUser(User user);
    User getUserById(Long id);
}
