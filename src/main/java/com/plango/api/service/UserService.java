package com.plango.api.service;

import com.plango.api.entity.User;

public interface UserService {
    void addOrUpdateUser(User user);
    User getUserById(Long id);
}
