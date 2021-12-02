package com.plango.api.entity;

import com.plango.api.common.types.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@Entity
@RestResource(exported = false)
@Table(name = "member")
public class Member extends BaseEntity {
    @NotNull
    @ManyToOne
    @JoinColumn(name = "userMember")
    private User userMember;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "travel")
    private Travel travel;

    @NotNull
    private Role role;

}
