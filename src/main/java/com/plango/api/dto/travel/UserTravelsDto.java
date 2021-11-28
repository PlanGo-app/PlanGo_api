package com.plango.api.dto.travel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserTravelsDto {
    List<GetTravelDto> travels;
}
