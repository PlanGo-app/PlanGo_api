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
    private PGpoint point;

    @NotNull // TODO change travel_id to travel
    @ManyToOne
    private Travel travel;

    @NotNull
    @Column(name = "created_by")
    @OneToOne
    private User createdBy;
}
