package com.plango.api.service;

import com.plango.api.entity.MemberList;
import com.plango.api.entity.User;
import com.plango.api.repository.MemberListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberListService {

    @Autowired
    MemberListRepository memberListRepository;

    public List<MemberList> getAllTravelsByUser(User user) {
        return memberListRepository.findAllByMember(user);
    }

    public void createMember(MemberList memberList){
        memberListRepository.save(memberList);
    }
}
