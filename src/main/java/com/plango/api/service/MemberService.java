package com.plango.api.service;

import com.plango.api.common.exception.UserNotFoundException;
import com.plango.api.common.types.Role;
import com.plango.api.dto.member.MemberDto;
import com.plango.api.dto.user.UserDto;
import com.plango.api.entity.Member;
import com.plango.api.entity.Travel;
import com.plango.api.entity.User;
import com.plango.api.repository.MemberRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ModelMapper modelMapper;

    public List<Member> getAllTravelsByUser(User user) {
        return memberRepository.findAllByUserMember(user);
    }

    public List<Member> getAllMembersByTravel(Travel travel) {
        return memberRepository.findAllByTravel(travel);
    }

    public Member getMemberByTravel(Travel travel, User user) throws UserNotFoundException {
        List<Member> member = memberRepository.findByTravelAndUserMember(travel, user);
        if(member.isEmpty()){
            throw new UserNotFoundException("User not found for current travel.");
        }
        return member.get(0);
    }

    public void createMember(Member newMember){
        memberRepository.save(newMember);
    }

    public void putMember(Member member, Role role){
        member.setRole(role);
        memberRepository.save(member);
    }

    public void deleteMember(Member member){
        memberRepository.delete(member);
    }

    public MemberDto convertToDto(Member member) {
        MemberDto memberDto = new MemberDto();
        UserDto userDto = modelMapper.map(member.getUserMember(), UserDto.class);
        memberDto.setUser(userDto);
        memberDto.setRole(member.getRole());
        return memberDto;
    }

    public boolean isMember(User user, Travel travel) {
        return !memberRepository.findByTravelAndUserMember(travel, user).isEmpty();
    }

    public Member convertToEntity(MemberDto memberDto) {
        return modelMapper.map(memberDto, Member.class);
    }
}
