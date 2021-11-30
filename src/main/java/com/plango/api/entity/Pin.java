package com.plango.api.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@Entity
public class Pin extends BaseEntity {
    @NotBlank
    private String name;

    @NotNull
    @Column(name = "longitude")
    private Float longitude;

    @NotNull
    @Column(name = "latitude")
    private Float latitude;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "travel")
    private Travel travel;

    @OneToOne
    @JoinColumn(name = "planning_event")
    private PlanningEvent planningEvent;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
}
