package com.flowtex.IAM.Domain.Model.Entities;

import com.flowtex.IAM.Domain.Model.ValueObjects.Roles;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false, length = 50)
    private Roles name;

    public Role() {
    }

    public Role(Roles name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public Roles getName() {
        return name;
    }

    public String getStringName() {
        return name.name();
    }
}
