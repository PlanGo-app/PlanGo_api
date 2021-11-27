package com.plango.api.repository;

import com.plango.api.entity.PlanningEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

@RestResource(exported = false)
@Repository
public interface PlanningEventRepository extends CrudRepository<PlanningEvent, Long> {
}
