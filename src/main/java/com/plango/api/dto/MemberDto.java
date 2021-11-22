package com.plango.api.dto;

import com.plango.api.common.types.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDto {
    UserDto user;
    Role role;
}
