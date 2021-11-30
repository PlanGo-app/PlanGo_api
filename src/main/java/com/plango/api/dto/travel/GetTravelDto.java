package com.plango.api.dto.travel;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetTravelDto {
    private Long id;
    private String country;
    private String city;
    private LocalDateTime dateStart;
    private LocalDateTime dateEnd;
    private String invitationCode;
}