package com.plango.api.dto.travel;

import com.plango.api.dto.pin.GetPinDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class TravelPinsDto {
    List<GetPinDto> pins;
}
