package com.plango.api.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.plango.api.common.component.IAuthenticationFacade;
import com.plango.api.common.constant.ExceptionMessage;
import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.common.exception.UserAlreadyExistsException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.common.types.Role;
import com.plango.api.dto.travel.GetTravelDto;
import com.plango.api.dto.user.UserDto;
import com.plango.api.dto.user.UserUpdateDto;
import com.plango.api.entity.Member;
import com.plango.api.entity.Travel;
import com.plango.api.entity.User;
import com.plango.api.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    ModelMapper modelMapper;

    @Mock
    PasswordEncoder encoder;

    @Mock
    UserRepository userRepository;

    @Mock
    IAuthenticationFacade authenticationFacade;

    @Mock
    MemberService memberService;

    private User currentUser;
    private User updatedCurrentUser;
    private UserDto currentUserDto;
    private UserUpdateDto userUpdateDto_WithModif;
    private UserUpdateDto userUpdateDto_WithoutModif;
    private GetTravelDto oneTravelDto;
    private List<GetTravelDto> listTravelDto;
    private Member oneMember;
    private List<Member> listMember;

    private static final Long CURRENT_USER_ID = 10L;
    private static final String CURRENT_USER_PSEUDO_PASSWORD = "currentUser";
    private static final String CURRENT_USER_EMAIL = "currentuser@test.com";
    private static final Long USER_THAT_DOESNT_EXIST_ID = 900L;
    private static final String USER_THAT_DOESNT_EXIST_PSEUDO = "userNonExisting";
    private static final String USER_EMAIL_UPDATE = "newcurrentusermail@test.com";
    private static final String USER_PWD_UPDATE = "newcurrentuserpwd";
    private static final Long USER_TRAVEL_ID = 50L;


    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        currentUser = buildCurrentUser();
        currentUserDto = buildCurrentUserDto();
        userUpdateDto_WithoutModif = new UserUpdateDto();
        userUpdateDto_WithModif = new UserUpdateDto();
        userUpdateDto_WithModif.setEmail(USER_EMAIL_UPDATE);
        userUpdateDto_WithModif.setPassword(USER_PWD_UPDATE);
        updatedCurrentUser = currentUser;
        updatedCurrentUser.setEmail(USER_EMAIL_UPDATE);
        updatedCurrentUser.setPassword(USER_PWD_UPDATE);
        oneTravelDto = buildTravelDto();
        listTravelDto = new ArrayList<>();
        listTravelDto.add(oneTravelDto);
        oneMember = buildMember();
        listMember = new ArrayList<>();
        listMember.add(oneMember);
    }

    @Test
    void cannotGetCurrentUser_NotFound() throws CurrentUserAuthorizationException {
        when(authenticationFacade.getCurrentUser()).thenThrow(new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_CANNOT_BE_AUTHENTICATED));
    
        assertThatExceptionOfType(CurrentUserAuthorizationException.class)
                .isThrownBy(() -> userService.getCurrentUserDto())
                .withMessage(ExceptionMessage.CURRENT_USER_CANNOT_BE_AUTHENTICATED);
    }

    @Test
    void getCurrentUser_ConvertedIntoDto() throws CurrentUserAuthorizationException {
        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
        when(userService.convertUserEntityToDto(currentUser)).thenReturn(currentUserDto);
        
        UserDto responseDto = userService.getCurrentUserDto();

        verify(authenticationFacade).getCurrentUser();
        verify(modelMapper).map(currentUser, UserDto.class);
        assertEquals(responseDto, currentUserDto);
    }

    @Test
    void cannotGetUserById_NotFound() {
        when(userRepository.findById(USER_THAT_DOESNT_EXIST_ID)).thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> userService.getUserById(USER_THAT_DOESNT_EXIST_ID))
                .withMessageContaining("No user with id");
    }

    @Test
    void getUserById() throws UserNotFoundException {
        when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(currentUser));

        User user = userService.getUserById(CURRENT_USER_ID);
        assertEquals(currentUser, user);
    }

    @Test
    void cannotGetUserByPseudo_NotFound() {
        when(userRepository.findByPseudo(USER_THAT_DOESNT_EXIST_PSEUDO)).thenReturn(Optional.empty());

        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> userService.getUserByPseudo(USER_THAT_DOESNT_EXIST_PSEUDO))
                .withMessageContaining("No user with pseudo");
    }

    @Test
    void getUserByPseudo() throws UserNotFoundException {
        when(userRepository.findByPseudo(USER_THAT_DOESNT_EXIST_PSEUDO)).thenReturn(Optional.of(currentUser));

        User user = userService.getUserByPseudo(USER_THAT_DOESNT_EXIST_PSEUDO);
        assertEquals(currentUser, user);
    }

    @Test
    void createUserThatAlreadyExists() {
        when(userRepository.findByPseudo(CURRENT_USER_PSEUDO_PASSWORD)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(CURRENT_USER_EMAIL)).thenReturn(Optional.of(currentUser));

        assertThatExceptionOfType(UserAlreadyExistsException.class)
                .isThrownBy(() -> userService.createUser(currentUserDto))
                .withMessage(ExceptionMessage.PSEUDO_EMAIL_TAKEN);

        verify(userRepository).findByPseudo(CURRENT_USER_PSEUDO_PASSWORD);
        verify(userRepository).findByEmail(CURRENT_USER_EMAIL);

    }

    @Test
    void createUserThatDoesntAlreadyExists() throws UserAlreadyExistsException {
        when(userRepository.findByPseudo(CURRENT_USER_PSEUDO_PASSWORD)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(CURRENT_USER_EMAIL)).thenReturn(Optional.empty());
        when(modelMapper.map(currentUserDto, User.class)).thenReturn(currentUser);

        userService.createUser(currentUserDto);

        verify(userRepository).findByPseudo(CURRENT_USER_PSEUDO_PASSWORD);
        verify(userRepository).findByEmail(CURRENT_USER_EMAIL);
        verify(modelMapper).map(currentUserDto, User.class);
        verify(userRepository).save(currentUser);
    }

    @Test
    void updateUser_ThatExistsWithEmailAndPasswordModified() throws CurrentUserAuthorizationException {
        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
        when(encoder.encode(USER_PWD_UPDATE)).thenReturn(USER_PWD_UPDATE);

        userService.updateUser(userUpdateDto_WithModif);
        verify(authenticationFacade).getCurrentUser();
        verify(encoder).encode(USER_PWD_UPDATE);
        verify(userRepository).save(updatedCurrentUser);
    }

    @Test
    void updateUser_ThatExistsWithoutModifications() throws CurrentUserAuthorizationException {
        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
        userService.updateUser(userUpdateDto_WithoutModif);
        verify(authenticationFacade).getCurrentUser();
        verify(userRepository).save(currentUser);
    }

    @Test
    void updateUser_ThatDoesntExist() throws CurrentUserAuthorizationException {
        when(authenticationFacade.getCurrentUser()).thenThrow(new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_CANNOT_BE_AUTHENTICATED));
        assertThatExceptionOfType(CurrentUserAuthorizationException.class)
                .isThrownBy(() -> userService.updateUser(userUpdateDto_WithModif))
                .withMessage(ExceptionMessage.CURRENT_USER_CANNOT_BE_AUTHENTICATED);
    }

    @Test
    void deleteCurrentUser_ButDoesntExist() throws CurrentUserAuthorizationException {
        when(authenticationFacade.getCurrentUser()).thenThrow(new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_CANNOT_BE_AUTHENTICATED));
        assertThatExceptionOfType(CurrentUserAuthorizationException.class)
                .isThrownBy(() -> userService.deleteCurrentUser())
                .withMessage(ExceptionMessage.CURRENT_USER_CANNOT_BE_AUTHENTICATED);
    }

    @Test
    void deleteCurrentUser_WellDeleted() throws CurrentUserAuthorizationException {
        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
        userService.deleteCurrentUser();
        verify(userRepository).delete(currentUser);
    }

    @Test
    void getTravelsOfUserThatDoesntExist() throws CurrentUserAuthorizationException {
        when(authenticationFacade.getCurrentUser()).thenThrow(new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_CANNOT_BE_AUTHENTICATED));
        assertThatExceptionOfType(CurrentUserAuthorizationException.class)
                .isThrownBy(() -> userService.getTravels())
                .withMessage(ExceptionMessage.CURRENT_USER_CANNOT_BE_AUTHENTICATED);
    }

    @Test
    void getTravelsOfCurrentUser() throws UserNotFoundException, CurrentUserAuthorizationException {
        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
        when(memberService.getAllTravelsByUser(currentUser)).thenReturn(listMember);
        when(userService.convertTravelEntityToDto(oneMember.getTravel())).thenReturn(oneTravelDto);

        List<GetTravelDto> listTravels = userService.getTravels();

        verify(authenticationFacade).getCurrentUser();
        verify(memberService).getAllTravelsByUser(currentUser);
        verify(modelMapper).map(oneMember.getTravel(), GetTravelDto.class);
        assertEquals(listTravelDto, listTravels);
    }

    private User buildCurrentUser() {
        User user = new User();
        user.setId(CURRENT_USER_ID);
        user.setPseudo(CURRENT_USER_PSEUDO_PASSWORD);
        user.setPassword(CURRENT_USER_PSEUDO_PASSWORD);
        user.setEmail(CURRENT_USER_EMAIL);
        return user;
    }

    private UserDto buildCurrentUserDto() {
        UserDto userDto = new UserDto();
        userDto.setPseudo(CURRENT_USER_PSEUDO_PASSWORD);
        userDto.setPassword(CURRENT_USER_PSEUDO_PASSWORD);
        userDto.setEmail(CURRENT_USER_EMAIL);
        return userDto;
    }

    private GetTravelDto buildTravelDto() {
        GetTravelDto travel = new GetTravelDto();
        travel.setId(USER_TRAVEL_ID);
        return travel;
    }

    private Member buildMember() {
        Member member = new Member();
        member.setUserMember(currentUser);
        Travel travel = new Travel();
        travel.setId(USER_TRAVEL_ID);
        member.setTravel(travel);
        member.setRole(Role.ADMIN);
        return member;
    }
}
