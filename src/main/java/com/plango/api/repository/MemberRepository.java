package com.plango.api.repository;

import com.plango.api.entity.Member;
import com.plango.api.entity.Travel;
import com.plango.api.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends CrudRepository<Member, Long> {
    List<Member> findAllByUserMember(User user);
    List<Member> findAllByTravel(Travel travel);
}
