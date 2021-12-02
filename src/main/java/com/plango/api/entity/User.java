package com.plango.api.entity;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@RestResource(exported = false)
@Entity
public class User extends BaseEntity {
    @NotBlank
    @Column(unique=true)
    private String email;
    @NotBlank
    @Column(unique=true)
    private String pseudo;
    @NotBlank
    private String password;

    @OneToMany(mappedBy = "userMember", cascade = CascadeType.ALL)
    private List<Member> travels;
}