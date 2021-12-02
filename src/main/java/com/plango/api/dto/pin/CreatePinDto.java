package com.plango.api.dto.pin;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreatePinDto {
    private String name;
    private Float longitude;
    private Float latitude;
    private Long travelId;
}
