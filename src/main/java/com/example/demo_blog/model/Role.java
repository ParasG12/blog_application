package com.example.demo_blog.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, name = "role_name", unique = true)
    private AppRole roleName;

    // Bidirectional mapping: A role can have many users.
    @ManyToMany(mappedBy = "roles")
    @ToString.Exclude // To prevent circular references during logging
    private Set<Users> users = new HashSet<>();

    // Constructor for Role creation
    public Role(AppRole roleName) {
        this.roleName = roleName;
    }
}
