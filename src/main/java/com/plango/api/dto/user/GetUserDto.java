package com.plango.api.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserDto {
    private Long id;
    private String pseudo;
    private String email;
}
