package com.plango.api.repository;

import com.plango.api.entity.PlanningEvent;
import com.plango.api.entity.Travel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanningEventRepository extends CrudRepository<PlanningEvent, Long> {
    List<PlanningEvent> findAllByTravel(Travel travel);
}
