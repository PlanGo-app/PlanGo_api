package com.plango.api.entity;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Setter
@Getter
@Entity
@Table(name = "User")
public class User extends BaseEntity {
    @NotNull
    private String pseudo;
    private String email;
    @NotNull
    private String password;
}