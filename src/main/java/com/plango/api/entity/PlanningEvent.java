package com.plango.api.entity;

import com.plango.api.common.types.TransportType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@Entity
@Table(name = "planning_event")
public class PlanningEvent extends BaseEntity {
    @NotBlank
    private String name;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "travel")
    private Travel travel;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToOne
    @JoinColumn(name = "pin")
    private Pin pin;

    private TransportType transportTypeToNext;

    @OneToOne
    @JoinColumn(name = "event_after")
    private PlanningEvent eventAfter;

}
