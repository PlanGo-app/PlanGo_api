package com.plango.api.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "User")
public class User extends BaseEntity {
    private String pseudo;
    private String email;
    private String password;
}