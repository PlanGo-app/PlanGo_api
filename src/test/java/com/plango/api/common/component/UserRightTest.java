package com.plango.api.common.component;

import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.common.types.Role;
import com.plango.api.entity.Member;
import com.plango.api.entity.Travel;
import com.plango.api.entity.User;
import com.plango.api.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class UserRightTest {
    private static final Long CURRENT_USER_ID = 2L;
    private static final String CURRENT_USER_PSEUDO = "Current user pseudo";
    private static final Long TRAVEL_ID = 3L;

    @InjectMocks
    UserRight userRight = new UserRight();

    @Mock
    IAuthenticationFacade authenticationFacade;

    @Mock
    MemberService memberService;

    User currentUser;
    Travel travel;

    @BeforeEach
    void setUp() throws UserNotFoundException {
        MockitoAnnotations.openMocks(this);
        currentUser = new User();
        currentUser.setId(CURRENT_USER_ID);
        currentUser.setPseudo(CURRENT_USER_PSEUDO);
        travel = new Travel();
        travel.setId(TRAVEL_ID);
        when(authenticationFacade.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void shouldReturnTrue_onCurrentUserCanRead_whenCurrentUserIsMemberOfTravel() throws CurrentUserAuthorizationException {
        //ARRANGE
        when(memberService.isMember(currentUser, travel)).thenReturn(true);

        //ACT
        boolean result = userRight.currentUserCanRead(travel);

        //ASSERT
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalse_onCurrentUserCanRead_whenCurrentUserIsNotMemberOfTravel() throws CurrentUserAuthorizationException {
        //ARRANGE
        when(memberService.isMember(currentUser, travel)).thenReturn(false);

        //ACT
        boolean result = userRight.currentUserCanRead(travel);

        //ASSERT
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrue_onCurrentUserCanWrite_whenCurrentUserIsAdminOfTravel() throws CurrentUserAuthorizationException, UserNotFoundException {
        //ARRANGE
        Member member = new Member();
        member.setRole(Role.ADMIN);
        when(memberService.getMemberByTravel(travel, currentUser)).thenReturn(member);

        //ACT
        boolean result = userRight.currentUserCanWrite(travel);

        //ASSERT
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrue_onCurrentUserCanWrite_whenCurrentUserIsOrganizerOfTravel() throws CurrentUserAuthorizationException, UserNotFoundException {
        //ARRANGE
        Member member = new Member();
        member.setRole(Role.ORGANIZER);
        when(memberService.getMemberByTravel(travel, currentUser)).thenReturn(member);

        //ACT
        boolean result = userRight.currentUserCanWrite(travel);

        //ASSERT
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalse_onCurrentUserCanWrite_whenCurrentUserIsObserverOfTravel() throws CurrentUserAuthorizationException, UserNotFoundException {
        //ARRANGE
        Member member = new Member();
        member.setRole(Role.OBSERVER);
        when(memberService.getMemberByTravel(travel, currentUser)).thenReturn(member);

        //ACT
        boolean result = userRight.currentUserCanWrite(travel);

        //ASSERT
        assertThat(result).isFalse();
    }

}
