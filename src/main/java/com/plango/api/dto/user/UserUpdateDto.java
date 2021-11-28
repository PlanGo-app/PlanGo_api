package com.plango.api.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.plango.api.dto.user.UserBaseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto implements UserBaseDto {
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
