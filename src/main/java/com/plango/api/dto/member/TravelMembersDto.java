package com.plango.api.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TravelMembersDto {
    List<MemberDto> members;
}
