package com.plango.api.dto.planningevent;

import com.plango.api.common.types.TransportType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class GetPlanningEventDto implements PlanningEventDto{
    private Long id;
    private String name;
    private Long travelId;
    private Long createdBy;
    private Long pinId;
    private LocalDateTime dateStart;
    private LocalDateTime dateEnd;
    private TransportType transportTypeToNext;
    private Long eventAfterId;
}
