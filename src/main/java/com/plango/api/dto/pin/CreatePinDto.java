package com.plango.api.dto.pin;

import com.plango.api.entity.Travel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreatePinDto {
    private String name;
    private Float longitude;
    private Float latitude;
    private Travel travel;
}
