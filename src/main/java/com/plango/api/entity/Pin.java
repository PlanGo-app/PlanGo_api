package com.plango.api.entity;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import org.postgresql.geometric.PGpoint;

@Setter
@Getter
@Entity
public class Pin extends BaseEntity {
    @NotNull
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
