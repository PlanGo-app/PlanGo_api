package com.plango.api.repository;

import com.plango.api.entity.Pin;

import com.plango.api.entity.Travel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RestResource(exported = false)
@Repository
public interface PinRepository extends CrudRepository<Pin, Long> {
    Optional<Pin> findByTravelAndLongitudeAndLatitude(Travel travel, Float longitude, Float latitude);
    List<Pin> findAllByTravel(Travel travel);
}
