package com.plango.api.dto.member;

import com.plango.api.common.types.Role;
import com.plango.api.dto.user.GetUserDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDto {
    GetUserDto user;
    Role role;
}
