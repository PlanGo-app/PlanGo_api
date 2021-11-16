package com.plango.api.entity;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
public class Travel extends BaseEntity {
    @NotBlank
    private String name;
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
    @OneToOne
    @JoinColumn(name = "member_list")
    private MemberList members;
}