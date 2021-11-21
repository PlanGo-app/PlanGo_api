package com.plango.api.repository;

import com.plango.api.entity.PlanningEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanningEventRepository extends CrudRepository<PlanningEvent, Long> {

}
