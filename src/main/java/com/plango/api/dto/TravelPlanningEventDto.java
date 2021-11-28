package com.plango.api.dto;

import com.plango.api.dto.planningevent.GetPlanningEventDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class TravelPlanningEventDto {
    List<GetPlanningEventDto> planningEvents;
}
