package com.plango.api.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TravelDto {
    private String name;
    private String country;
    private String city;
    private LocalDateTime dateStart;
    private LocalDateTime dateEnd;
}