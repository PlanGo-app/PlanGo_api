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
    public void addUser(User user) throws Exception {
        boolean pseudoExists = pseudoTaken(user.getPseudo());
        boolean emailExists = emailTaken(user.getEmail());

        if(pseudoExists || emailExists){
            throw new Exception("Pseudo or email already taken");
        }
        else {
            userRepository.save(user);
        }
    }

    @Override
    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if(user != null){
            System.out.println(user.getPseudo());
        }
        return user;
    }

    @Override
    public boolean pseudoTaken(String pseudo){
        return userRepository.findByPseudo(pseudo).isPresent();
    }

    @Override
    public boolean emailTaken(String email){
        return userRepository.findByEmail(email).isPresent();
    }
}
