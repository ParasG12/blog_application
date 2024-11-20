package com.example.demo_blog.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;
    private String email;
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comments> comments = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Posts> posts = new ArrayList<>();

    // Bidirectional relationship: User has many roles, and each role has many users
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public Users(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    // Helper method to add a role to the user and ensure bidirectional relationship
    public void addRole(Role role) {
        this.roles.add(role);
        role.getUsers().add(this); // Ensure that the role also has the user in its users set
    }

    // Helper method to remove a role from the user and ensure bidirectional relationship
    public void removeRole(Role role) {
        this.roles.remove(role);
        role.getUsers().remove(this); // Ensure that the role's users set is updated
    }
}
