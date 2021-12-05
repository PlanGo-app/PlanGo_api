package com.plango.api.entity;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@RestResource(exported = false)
@Entity
public class Travel extends BaseEntity {
    @NotBlank
    private String country;
    private String city;

    @Column(name = "date_start")
    private LocalDateTime dateStart;

    @Column(name = "date_end")
    private LocalDateTime dateEnd;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @NotNull
    @Column(name = "invitation_code")
    private String invitationCode;

    @OneToMany(mappedBy = "travel", cascade = CascadeType.ALL)
    private List<Pin> pins;

    @OneToMany(mappedBy = "travel", cascade = CascadeType.ALL)
    private List<PlanningEvent> planningEvents;

    @NotNull
    @OneToMany(mappedBy = "travel", cascade = CascadeType.ALL)
    private List<Member> members;
}