package com.plango.api.service.impl;

import com.plango.api.entity.User;
import com.plango.api.repository.UserRepository;
import com.plango.api.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;


    @Override
    public void addUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        System.out.println(user.getPseudo());
        return user;
    }
}
