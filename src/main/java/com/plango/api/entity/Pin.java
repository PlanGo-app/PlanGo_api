package com.plango.api.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.postgresql.geometric.PGpoint;

@Setter
@Getter
@Entity
public class Pin extends BaseEntity {
    @NotBlank
    private String name;

    @NotNull
    @Column(name = "localisation")
    private PGpoint point;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "travel")
    private Travel travel;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
}
