package com.plango.api.repository;

import com.plango.api.entity.MemberList;
import com.plango.api.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberListRepository extends CrudRepository<MemberList, Long> {
    List<MemberList> findAllByMember(User user);
}
