package com.plango.api.repository;

import com.plango.api.entity.Travel;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RestResource(exported = false)
@Repository
public interface TravelRepository extends CrudRepository<Travel, Long> {
    boolean existsByInvitationCode(String code);
    Optional<Travel> findByInvitationCode(String code);
}
