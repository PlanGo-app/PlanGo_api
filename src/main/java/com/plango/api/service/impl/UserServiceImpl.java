package com.plango.api.service.impl;

import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.common.exception.UserAlreadyExistsException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.common.component.IAuthenticationFacade;
import com.plango.api.entity.User;
import com.plango.api.repository.UserRepository;
import com.plango.api.security.UserAuthDetails;
import com.plango.api.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    IAuthenticationFacade authenticationFacade;

    @Override
    public User getUserById(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElse(null);
        if(user == null){
            throw new UserNotFoundException(String.format("No user with id: %s were found", id));
        }
        return user;
    }

    @Override
    public User getUserByPseudo(String pseudo) throws UserNotFoundException {
        User user = userRepository.findByPseudo(pseudo).orElse(null);
        if(user == null){
            throw new UserNotFoundException(String.format("No user with pseudo: %s were found", pseudo));
        }
        return user;
    }

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
    public void updateUser(Long id, User user) throws UserNotFoundException, CurrentUserAuthorizationException {
        User userOnUpdate = getUserById(id);
        if(!userHasRight(userOnUpdate, authenticationFacade.getCurrentUserAuthDetails())) {
            throw new CurrentUserAuthorizationException("Current user not authorized");
        }
        if(user.getEmail() != null) {
            userOnUpdate.setEmail(user.getEmail());
        }
        if(user.getPassword() != null) {
            userOnUpdate.setPassword(user.getPassword());
        }
        userRepository.save(userOnUpdate);
    }

    @Override
    public void deleteUser(Long id) throws UserNotFoundException, CurrentUserAuthorizationException {
        User user = getUserById(id);
        if(!userHasRight(user, authenticationFacade.getCurrentUserAuthDetails())) {
            throw new CurrentUserAuthorizationException("Current user not authorized");
        }
        userRepository.delete(user);
    }

    private boolean pseudoTaken(String pseudo){
        return userRepository.findByPseudo(pseudo).isPresent();
    }

    private boolean emailTaken(String email){
        return userRepository.findByEmail(email).isPresent();
    }

    private boolean userHasRight(User user, UserAuthDetails userAuthDetails) {
        return user.getPseudo().equals(userAuthDetails.getUsername());
    }
}
