package com.plango.api.dto.travel;

import com.plango.api.dto.planningevent.GetPlanningEventDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class TravelPlanningEventsDto {
    List<GetPlanningEventDto> planningEvents;
}
