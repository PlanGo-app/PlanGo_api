package com.plango.api.dto.travel;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateTravelDto {
    private String country;
    private String city;
    private LocalDateTime dateStart;
    private LocalDateTime dateEnd;
}