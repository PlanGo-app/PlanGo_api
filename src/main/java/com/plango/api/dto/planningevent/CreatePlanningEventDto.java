package com.plango.api.dto.planningevent;

import com.plango.api.common.types.TransportType;
import com.plango.api.entity.Pin;
import com.plango.api.entity.PlanningEvent;
import com.plango.api.entity.Travel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreatePlanningEventDto implements PlanningEventDto{
    private String name;
    private Travel travel;
    private Pin pin;
    private LocalDateTime dateStart;
    private LocalDateTime dateEnd;
    private TransportType transportTypeToNext;
    private PlanningEvent eventAfter;
}
