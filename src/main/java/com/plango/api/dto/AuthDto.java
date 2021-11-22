package com.plango.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthDto {
    private String token;
    private Long userId;
}
