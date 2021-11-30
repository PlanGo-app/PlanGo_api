package com.plango.api.dto.pin;

import com.plango.api.entity.PlanningEvent;
import com.plango.api.entity.Travel;
import com.plango.api.entity.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GetPinDto {
    private Long id;
    private String name;
    private Float longitude;
    private Float latitude;
    private Travel travel;
    private PlanningEvent planningEvent;
    private User createdBy;
}
