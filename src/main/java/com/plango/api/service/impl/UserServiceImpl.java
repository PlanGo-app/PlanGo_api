package com.plango.api.service.impl;

import com.plango.api.entity.User;
import com.plango.api.repository.UserRepository;
import com.plango.api.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public void addOrUpdateUser(User user) {
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
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByPseudo(username).orElseThrow(() -> new UsernameNotFoundException("User " + username + " doesn't exist."));
		return user;
	}
}
