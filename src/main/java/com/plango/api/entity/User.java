package com.plango.api.entity;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@IdClass(UserCompositeKey.class)
public class User extends BaseEntity {
    @Id
    private String email;
    @Id
    private String pseudo;
    @NotNull
    private String password;
}