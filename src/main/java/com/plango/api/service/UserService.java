package com.plango.api.service;

import com.plango.api.common.exception.UserAlreadyExistsException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.common.component.IAuthenticationFacade;
import com.plango.api.dto.travel.GetTravelDto;
import com.plango.api.dto.user.UserBaseDto;
import com.plango.api.dto.user.UserDto;
import com.plango.api.dto.user.UserUpdateDto;
import com.plango.api.common.constant.ExceptionMessage;
import com.plango.api.entity.Member;
import com.plango.api.entity.Travel;
import com.plango.api.entity.User;
import com.plango.api.repository.UserRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    IAuthenticationFacade authenticationFacade;

    @Autowired
    MemberService memberService;

    public UserDto getCurrentUser() throws UserNotFoundException {
        return convertUserEntityToDto(authenticationFacade.getCurrentUser());
    }

    public User getUserById(Long userId) throws UserNotFoundException {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            throw new UserNotFoundException(String.format("No user with id: %d were found", userId));
        }
        return user;
    }

    public User getUserByPseudo(String pseudo) throws UserNotFoundException {
        User user = userRepository.findByPseudo(pseudo).orElse(null);
        if(user == null){
            throw new UserNotFoundException(String.format("No user with pseudo: %s were found", pseudo));
        }
        return user;
    }

    public void createUser(UserDto userDto) throws UserAlreadyExistsException {
        userDto.setEmail(userDto.getEmail().toLowerCase());
        if ( pseudoTaken(userDto.getPseudo()) || emailTaken(userDto.getEmail().toLowerCase()) ){
            throw new UserAlreadyExistsException(ExceptionMessage.PSEUDO_EMAIL_TAKEN);
        } else {
            userRepository.save(convertUserDtoToEntity(userDto));
        }
    }

    public void updateUser(UserUpdateDto userUpdateDto) throws UserNotFoundException {
        User userOnUpdate = authenticationFacade.getCurrentUser();
        if(userUpdateDto.getEmail() != null) {
            userOnUpdate.setEmail(userUpdateDto.getEmail());
        }
        if(userUpdateDto.getPassword() != null) {
            userOnUpdate.setPassword(encoder.encode(userUpdateDto.getPassword()));
        }
        userRepository.save(userOnUpdate);
    }

    public void deleteCurrentUser() throws UserNotFoundException {
        User user = authenticationFacade.getCurrentUser();
        userRepository.delete(user);
    }

    public List<GetTravelDto> getTravels() throws UserNotFoundException {
        User user = authenticationFacade.getCurrentUser();
        List<Member> listParticipations = memberService.getAllTravelsByUser(user);
        List<GetTravelDto> listTravels = new ArrayList<>();
        for(Member listParticipation : listParticipations){
            listTravels.add(convertTravelEntityToDto(listParticipation.getTravel()));
        }
        return listTravels;
    }

    public GetTravelDto convertTravelEntityToDto(Travel travel) {
        return modelMapper.map(travel, GetTravelDto.class);
    }

    public UserDto convertUserEntityToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public User convertUserDtoToEntity(UserBaseDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        if (user.getPassword() != null) {
            user.setPassword(encoder.encode(userDto.getPassword()));
        }
        return user;
    }

    private boolean pseudoTaken(String pseudo){
        return userRepository.findByPseudo(pseudo).isPresent();
    }

    private boolean emailTaken(String email){
        return userRepository.findByEmail(email).isPresent();
    }
}
