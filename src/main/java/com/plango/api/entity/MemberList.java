package com.plango.api.entity;

import com.plango.api.common.types.Role;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@Entity
@Table(name = "member_list")
public class MemberList extends BaseEntity {
    @NotNull
    @ManyToOne
    @JoinColumn(name = "member")
    private User member;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "travel")
    private Travel travel;

    @NotNull
    private Role role;

}
