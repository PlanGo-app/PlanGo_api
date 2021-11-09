package com.plango.api.entity;

import com.plango.api.common.types.Role;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "member_list")
public class MemberList extends BaseEntity {
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @NotNull
    @OneToOne
    @JoinColumn(name = "travel")
    private Travel travel;

    @NotNull
    private Role role;

}
