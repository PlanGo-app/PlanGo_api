package com.plango.api.entity;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "Travel")
public class Travel extends BaseEntity {
    @NotNull
    private String name;
    private String country;
    private String city;

    @Column(name = "date_start")
    private LocalDateTime dateStart;

    @Column(name = "date_end")
    private LocalDateTime dateEnd;

    @NotNull
    @Column(name = "created_by")
    @OneToOne
    private User createdBy;

    @NotNull
    @Column(name = "member_list")
    @OneToMany
    private List<User> members;
}