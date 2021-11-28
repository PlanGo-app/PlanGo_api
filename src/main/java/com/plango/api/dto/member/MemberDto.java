package com.plango.api.dto.member;

import com.plango.api.common.types.Role;
import com.plango.api.dto.user.UserDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDto {
    UserDto user;
    Role role;
}
