package com.plango.api.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@RestResource(exported = false)
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel")
    private Travel travel;

    @OneToOne(mappedBy = "pin", cascade = CascadeType.ALL)
    @JoinColumn(name = "planning_event")
    private PlanningEvent planningEvent;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
}
