package com.plango.api.entity;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Setter
@Getter
@Entity
@Table(name = "Trip")
public class Trip extends BaseEntity {
    @NotNull
    private String destination;
    @NotNull
    private User creator;
}