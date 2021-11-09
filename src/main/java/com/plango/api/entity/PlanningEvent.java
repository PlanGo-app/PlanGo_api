package com.plango.api.entity;

import com.plango.api.common.types.TransportType;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "planning_event")
public class PlanningEvent extends BaseEntity {
    @NotNull
    private String name;

    @NotNull
    @ManyToOne
    private Travel travel;

    @NotNull
    @ManyToOne
    private User createdBy;

    @OneToOne
    private Pin pin;

    private TransportType transportTypeToNext;

    @OneToOne
    private PlanningEvent eventAfter;

}
