package com.plango.api.entity;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
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
}