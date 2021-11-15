package com.plango.api.entity;

import com.plango.api.common.types.Role;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Entity
@Table(name = "member_list")
public class MemberList extends BaseEntity {
    @NotBlank
    @ManyToOne
    @JoinColumn(name = "member")
    private User user;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "travel")
    private Travel travel;

    @NotBlank
    private Role role;

}
