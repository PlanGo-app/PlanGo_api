package com.plango.api.entity;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class User extends BaseEntity {
    @NotNull
    private String pseudo;
    @NotNull
    private String email;
    @NotNull
    private String password;
}