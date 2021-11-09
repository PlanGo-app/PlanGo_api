package com.plango.api.entity;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.postgresql.geometric.PGpoint;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "Pin")
public class Pin extends BaseEntity {
    @NotNull
    private String name;

    @NotNull // TODO change long_lat to point in db
    @Column(name = "long_lat")
    private PGpoint point;

    @NotNull // TODO change travel_id to travel
    @Column(name = "travel_id")
    @ManyToOne
    private Travel travel;

    @NotNull
    @OneToOne
    private User createdBy;
}
