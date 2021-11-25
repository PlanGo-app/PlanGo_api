package com.plango.api.dto.planningevent;

import com.plango.api.common.types.TransportType;
import com.plango.api.entity.PlanningEvent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class UpdatePlanningEventDto {
    private Long id;
    private String name;
    private LocalDateTime dateStart;
    private LocalDateTime dateEnd;
    private TransportType transportTypeToNext;
    private PlanningEvent eventAfter;
}
