package com.plango.api.service;

import com.plango.api.dto.MemberDto;
import com.plango.api.dto.UserDto;
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
        List<Member> members = memberRepository.findAllByTravel(travel);
        return null;
    }

    public void createMember(Member newMember){
        memberRepository.save(newMember);
    }

    public MemberDto convertToDto(Member member) {
        MemberDto memberDto = new MemberDto();
        UserDto userDto = modelMapper.map(member.getUserMember(), UserDto.class);
        memberDto.setUser(userDto);
        memberDto.setRole(member.getRole());
        return memberDto;
    }

    public Member convertToEntity(MemberDto memberDto) {
        return modelMapper.map(memberDto, Member.class);
    }
}
