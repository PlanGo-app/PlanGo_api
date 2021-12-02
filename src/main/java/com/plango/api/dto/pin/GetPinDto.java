package com.plango.api.dto.pin;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GetPinDto {
    private Long id;
    private String name;
    private Float longitude;
    private Float latitude;
    private Long travelId;
    private Long planningEventId;
    private Long createdBy;
}
