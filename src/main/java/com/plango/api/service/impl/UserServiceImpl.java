package com.plango.api.service.impl;

import com.plango.api.common.exception.UserNotFoundException;
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
    public void createUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void updateUser(Long id, User user) throws UserNotFoundException {
        getUserById(id);
        userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElse(null);
        if(user == null){
            throw new UserNotFoundException(String.format("No user with id: %s were found", id));
        }
        return user;
    }

    @Override
    public void deleteUser(Long id) throws UserNotFoundException {
        User user = getUserById(id);
        // TODO check if user has right
        userRepository.delete(user);
    }
}
