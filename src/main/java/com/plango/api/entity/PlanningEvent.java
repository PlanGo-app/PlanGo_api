package com.plango.api.entity;

import com.plango.api.common.types.TransportType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Setter
@Getter
@RestResource(exported = false)
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

    @OneToOne(mappedBy = "planningEvent")
    @JoinColumn(name = "pin")
    private Pin pin;

    @Column(name = "date_start")
    private LocalDateTime dateStart;

    @Column(name = "date_end")
    private LocalDateTime dateEnd;

    private TransportType transportTypeToNext;

    @OneToOne
    @JoinColumn(name = "event_after")
    private PlanningEvent eventAfter;

}
