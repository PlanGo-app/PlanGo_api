package com.plango.api.repository;

import com.plango.api.entity.Travel;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelRepository extends CrudRepository<Travel, Long> {
}
