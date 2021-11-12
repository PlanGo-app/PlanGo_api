package com.plango.api.security;

import com.plango.api.entity.User;
import com.plango.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsAuthService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByPseudo(username).orElseThrow(() -> new UsernameNotFoundException("User " + username + " doesn't exist."));
		return userToUserDetails(user);
	}

	public User userDetailsToUser(UserDetails userDetails){
        return userRepository.findByPseudo(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User " + userDetails.getUsername() + " doesn't exist."));
    }

    public UserDetails userToUserDetails(User user){
	    UserAuthDetails userAuthDetails = new UserAuthDetails();
	    userAuthDetails.setUsername(user.getPseudo());
	    userAuthDetails.setPassword(user.getPassword());
	    return userAuthDetails;
    }
}
