package com.plango.api.service;

import com.plango.api.common.component.IAuthenticationFacade;
import com.plango.api.common.exception.TravelNotFoundException;
import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.common.types.Role;
import com.plango.api.dto.MemberDto;
import com.plango.api.dto.TravelMembersDto;
import com.plango.api.entity.Member;
import com.plango.api.entity.Travel;
import com.plango.api.entity.User;
import com.plango.api.repository.TravelRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TravelService {

    @Autowired
    TravelRepository travelRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    IAuthenticationFacade authenticationFacade;

    public Travel getTravelById(Long id) throws TravelNotFoundException {
        Travel travel = travelRepository.findById(id).orElse(null);
        if(travel == null){
            throw new TravelNotFoundException(String.format("No travel with id: %s were found", id));
        }
        return travel;
    }

    public void createTravel(Travel newTravel) throws UserNotFoundException {
        User currentUser = authenticationFacade.getCurrentUser();
        newTravel.setCreatedBy(currentUser);
        Travel travel = travelRepository.save(newTravel);
        this.addMember(travel, currentUser, Role.ADMIN);
    }

    public void addMember(Travel travel, User user, Role userRole) throws UserNotFoundException {
        Member newMember = new Member();
        newMember.setUserMember(user);
        newMember.setTravel(travel);
        newMember.setRole(userRole);
        memberService.createMember(newMember);
    }

    public TravelMembersDto getMembers(Long id) throws TravelNotFoundException {
        Travel travel = travelRepository.findById(id).orElse(null);
        if(travel == null){
            throw new TravelNotFoundException(String.format("No travel with id: %s were found", id));
        }
        List<Member> listParticipants = memberService.getAllMembersByTravel(travel);
        List<MemberDto> membersDto = new ArrayList<>();
        for(Member participant : listParticipants) {
            membersDto.add(memberService.convertToDto(participant));
        }
        return new TravelMembersDto(membersDto);
    }

}
