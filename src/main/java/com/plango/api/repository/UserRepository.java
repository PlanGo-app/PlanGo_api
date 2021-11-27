package com.plango.api.repository;

import com.plango.api.entity.User;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

@RestResource(exported = false)
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByPseudo(String pseudo);
    Optional<User> findByEmail(String email);
}
