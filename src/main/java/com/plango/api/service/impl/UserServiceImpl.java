package com.plango.api.service.impl;

import com.plango.api.common.exception.UserAlreadyExistsException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.dto.UserDto;
import com.plango.api.entity.User;
import com.plango.api.repository.UserRepository;
import com.plango.api.service.UserService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public void createUser(User user)  throws UserAlreadyExistsException {
        if ( pseudoTaken(user.getPseudo()) ){
            throw new UserAlreadyExistsException("Pseudo already taken");
        } else if ( emailTaken(user.getEmail()) ) {
            throw new UserAlreadyExistsException("Email already taken");
        } else {
            userRepository.save(user);
        }
    }

    @Override
    public void updateUser(User user) throws UserNotFoundException {
        getUserById(user.getId());
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


    private boolean pseudoTaken(String pseudo){
        return userRepository.findByPseudo(pseudo).isPresent();
    }

    private boolean emailTaken(String email){
        return userRepository.findByEmail(email).isPresent();
    }
}
