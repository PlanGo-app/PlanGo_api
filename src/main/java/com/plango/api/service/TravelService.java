package com.plango.api.service;

import com.plango.api.common.component.CodeGenerator;
import com.plango.api.common.component.IAuthenticationFacade;
import com.plango.api.common.component.UserRight;
import com.plango.api.common.constant.ExceptionMessage;
import com.plango.api.common.exception.CurrentUserAuthorizationException;
import com.plango.api.common.exception.TravelNotFoundException;
import com.plango.api.common.exception.UserIsAlreadyMemberException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.common.types.Role;
import com.plango.api.dto.member.MemberDto;
import com.plango.api.dto.pin.GetPinDto;
import com.plango.api.dto.travel.CreateTravelDto;
import com.plango.api.dto.travel.GetTravelDto;
import com.plango.api.dto.member.TravelMembersDto;
import com.plango.api.dto.travel.TravelPinsDto;
import com.plango.api.dto.travel.TravelPlanningEventsDto;
import com.plango.api.dto.planningevent.GetPlanningEventDto;
import com.plango.api.entity.Member;
import com.plango.api.entity.Travel;
import com.plango.api.entity.User;
import com.plango.api.repository.TravelRepository;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TravelService {

    @Autowired
    TravelRepository travelRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    PlanningEventService planningEventService;

    @Autowired
    PinService pinService;

    @Autowired
    IAuthenticationFacade authenticationFacade;

    @Autowired
    CodeGenerator codeGenerator;

    @Autowired
    UserRight userRight;

    @Autowired
    ModelMapper modelMapper;

    public Travel getTravelById(Long id) throws TravelNotFoundException {
        return findTravelById(id);
    }

    public GetTravelDto createTravel(Travel newTravel) throws CurrentUserAuthorizationException {
        User currentUser = authenticationFacade.getCurrentUser();
        newTravel.setCreatedBy(currentUser);
        newTravel.setInvitationCode(this.generateUniqueInvitationCode());
        Travel travel = travelRepository.save(newTravel);
        try {
            this.addMember(travel, currentUser, Role.ADMIN);
        } catch (UserIsAlreadyMemberException e) {
            log.error("Error creating the travel, current user is already member.", e);
        }
        return convertToGetDto(travel);
    }

    public void deleteTravelById(Long id) throws TravelNotFoundException, CurrentUserAuthorizationException {
        getTravelById(id);
        if(!userRight.currentUserCanDeleteTravel(id)) {
            throw new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_NOT_ALLOWED_TO_DELETE_TRAVEL);
        }
        travelRepository.deleteById(id);
    }

    public void addMember(Travel travel, User user, Role userRole) throws CurrentUserAuthorizationException, UserIsAlreadyMemberException {
        if(memberService.isMember(user, travel)) {
            throw new UserIsAlreadyMemberException(ExceptionMessage.USER_IS_ALREADY_MEMBER_OF_THE_TRAVEL);
        }
        if(authenticationFacade.getCurrentUser() != user){
            this.checkOrganizerRoleCurrentUser(travel);
        }
        Member newMember = new Member();
        newMember.setUserMember(user);
        newMember.setTravel(travel);
        newMember.setRole(userRole);
        memberService.createMember(newMember);
    }

    public void updateMember(Travel travel, User user, Role userRole) throws CurrentUserAuthorizationException, UserNotFoundException {
        this.checkAdminRoleCurrentUser(travel);
        memberService.putMember(memberService.getMemberByTravel(travel, user), userRole);
    }

    public void deleteMember(Travel travel, User user) throws CurrentUserAuthorizationException, UserNotFoundException {
        if(authenticationFacade.getCurrentUser() != user){
            this.checkAdminRoleCurrentUser(travel);
        }
        memberService.deleteMember(memberService.getMemberByTravel(travel, user));
    }

    public void deleteCurrentMemberOfTravel(Long travelId) throws UserNotFoundException, TravelNotFoundException, CurrentUserAuthorizationException {
        Travel travel = getTravelById(travelId);
        User currentUser = authenticationFacade.getCurrentUser();
        Member member = memberService.getMemberByTravel(travel, currentUser);
        List<Member> listParticipants = memberService.getAllMembersByTravel(travel);
        if(listParticipants.size() <= 1) { // If there are no membre left in this travel, remove it
            travelRepository.deleteById(travelId);
        }
        else if(member.getRole().equals(Role.ADMIN)) { // If current user was admin, delegate this role to an organizer
            boolean adminAsNotBeenDelegated = true;
            User newAdmin = member.getUserMember();
            for(Member memberOfTravel:listParticipants) {
                if(adminAsNotBeenDelegated && memberOfTravel.getRole().equals(Role.ORGANIZER)) {
                    memberService.putMember(memberOfTravel, Role.ADMIN);
                    newAdmin = memberOfTravel.getUserMember();
                    adminAsNotBeenDelegated = false;
                }
            }
            if(adminAsNotBeenDelegated) {
                travelRepository.deleteById(travelId);
            } else {
                travel.setCreatedBy(newAdmin);
                memberService.deleteMember(member);
            }
        } else {
            memberService.deleteMember(member);
        }
    }

    public TravelMembersDto getMembers(Long id) throws TravelNotFoundException {
        Travel travel = findTravelById(id);
        List<Member> listParticipants = memberService.getAllMembersByTravel(travel);
        List<MemberDto> membersDto = new ArrayList<>();
        for(Member participant : listParticipants) {
            membersDto.add(memberService.convertToDto(participant));
        }
        return new TravelMembersDto(membersDto);
    }

    public GetTravelDto getTravelByInvitationCode(String code) throws TravelNotFoundException {
        Travel travel = travelRepository.findByInvitationCode(code).orElse(null);
        if(travel == null){
            throw new TravelNotFoundException("No travel found with given code.");
        }
        return convertToGetDto(travel);
    }

    public TravelPlanningEventsDto getTravelPlanningEvents(Long travelId) throws TravelNotFoundException, CurrentUserAuthorizationException {
        if(!userRight.currentUserCanRead(travelId)) {
            throw new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_NOT_ALLOWED_TO_GET_PLANNING_EVENT);
        }
        Travel travel = findTravelById(travelId);
        List<GetPlanningEventDto> planningEventDtoList = planningEventService.getPlanningEventByTravel(travel);
        return new TravelPlanningEventsDto(planningEventDtoList);
    }

    public TravelPinsDto getTravelPins(Long travelId) throws TravelNotFoundException, CurrentUserAuthorizationException {
        if(!userRight.currentUserCanRead(travelId)) {
            throw new CurrentUserAuthorizationException(ExceptionMessage.CURRENT_USER_NOT_ALLOWED_TO_GET_PIN);
        }
        Travel travel = findTravelById(travelId);
        List<GetPinDto> pinDtoList = pinService.getPinByTravel(travel);
        return new TravelPinsDto(pinDtoList);
    }

    private Travel findTravelById(Long id) throws TravelNotFoundException {
        Travel travel = travelRepository.findById(id).orElse(null);
        if(travel == null){
            throw new TravelNotFoundException(String.format("No travel with id: %s were found", id));
        }
        return travel;
    }

    private void checkOrganizerRoleCurrentUser(Travel travel) throws CurrentUserAuthorizationException {
        try {
            User currentUser = authenticationFacade.getCurrentUser();
            Member currentMember = memberService.getMemberByTravel(travel, currentUser);
            if(currentMember.getRole() == Role.OBSERVER){
                throw new CurrentUserAuthorizationException("Current user doesn't have organizer rights.");
            }
        }
        catch(UserNotFoundException e) {
            throw new CurrentUserAuthorizationException(e.getMessage());
        }
    }

    private void checkAdminRoleCurrentUser(Travel travel) throws CurrentUserAuthorizationException {
        try {
            User currentUser = authenticationFacade.getCurrentUser();
            Member currentMember = memberService.getMemberByTravel(travel, currentUser);
            if(currentMember.getRole() != Role.ADMIN){
                throw new CurrentUserAuthorizationException("Current user doesn't have admin rights.");
            }
        }
        catch(UserNotFoundException e) {
            throw new CurrentUserAuthorizationException(e.getMessage());
        }
    }

    private String generateUniqueInvitationCode(){
        String invitation = codeGenerator.randomInvitationCode();
        if(travelRepository.existsByInvitationCode(invitation)){
            return generateUniqueInvitationCode();
        }
        return invitation;
    }

    public Travel convertCreateDtoToEntity(CreateTravelDto travelDto) {
        return modelMapper.map(travelDto, Travel.class);
    }

    public GetTravelDto convertToGetDto(Travel travel) {
        return modelMapper.map(travel, GetTravelDto.class);
    }

}
