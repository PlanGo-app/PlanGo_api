package com.plango.api.common.mapper;

import com.plango.api.dto.pin.GetPinDto;
import com.plango.api.dto.planningevent.GetPlanningEventDto;
import com.plango.api.entity.Pin;
import com.plango.api.entity.PlanningEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CustMapper {

    @Mapping(target = "travelId", source = "travel.id")
    @Mapping(target = "createdBy", source = "createdBy.id")
    @Mapping(target = "pinId", source = "pin.id")
    @Mapping(target = "eventAfterId", source = "eventAfter.id")
    GetPlanningEventDto planningEventToGetPlanningEventDto(PlanningEvent planningEvent);

    @Mapping(target = "travelId", source = "travel.id")
    @Mapping(target = "createdBy", source = "createdBy.id")
    @Mapping(target = "planningEventId", source = "planningEvent.id")
    GetPinDto pinToGetPinDto(Pin pin);
}
